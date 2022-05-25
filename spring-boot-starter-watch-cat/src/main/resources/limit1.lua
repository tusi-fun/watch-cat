--- 获取令牌 2019.12.17 12:33:50
--- 返回码
--- 0 没有令牌桶配置
--- -1 表示取令牌失败，也就是桶里没有令牌
--- 1 表示取令牌成功
--- @param key 令牌的唯一标识
--- @param permits  请求令牌数量
--- @param curr_mill_second 当前时间
local function acquire(key, permits, curr_mill_second)
    if tonumber(redis.pcall("EXISTS", key)) < 1 then --- 未配置令牌桶
        return 0
    end

    --- 令牌桶内数据：
    --- last_mill_second  最后一次放入令牌时间
    --- curr_permits  当前桶内令牌数
    --- max_permits   桶内令牌最大数量（最大令牌数 也就是并发数，比如10万）
    --- rate  令牌放置速度
    local rate_limit_info = redis.pcall("HMGET", key, "last_mill_second", "curr_permits", "max_permits", "rate")
    local last_mill_second = rate_limit_info[1]
    local curr_permits = tonumber(rate_limit_info[2])
    local max_permits = tonumber(rate_limit_info[3])
    local rate = rate_limit_info[4]

    --- 标识没有配置令牌桶
    if type(max_permits) == 'boolean' or max_permits == nil then
        return 0
    end
    --- 若令牌桶参数没有配置，则返回0
    if type(rate) == 'boolean' or rate == nil then
        return 0
    end

    local local_curr_permits = max_permits;
    --- 令牌桶刚刚创建，上一次获取令牌的毫秒数为空
    --- 根据和上一次向桶里添加令牌的时间和当前时间差，触发式往桶里添加令牌，并且更新上一次向桶里添加令牌的时间
    --- 如果向桶里添加的令牌数不足一个，则不更新上一次向桶里添加令牌的时间
    --- ~=号在Lua脚本的含义就是不等于!=
    if (type(last_mill_second) ~= 'boolean'  and last_mill_second ~= nil) then
        if(curr_mill_second - last_mill_second < 0) then
            return -1
        end
        --- 生成令牌操作
        --- 最关键代码：根据时间差计算令牌数量并匀速的放入令牌
        local reverse_permits = math.floor(((curr_mill_second - last_mill_second) / 1000) * rate)
        local expect_curr_permits = reverse_permits + curr_permits;
        --- 如果期望令牌数大于桶容量，则设为桶容量
        local_curr_permits = math.min(expect_curr_permits, max_permits);
        --- 大于0表示这段时间产生令牌，则更新最新令牌放入时间
        if (reverse_permits > 0) then
            redis.pcall("HSET", key, "last_mill_second", curr_mill_second)
        end
    else
        redis.pcall("HSET", key, "last_mill_second", curr_mill_second)
    end
    --- 取出令牌操作
    local result = -1
    if (local_curr_permits - permits >= 0) then
        result = 1
        redis.pcall("HSET", key, "curr_permits", local_curr_permits - permits)
    else
        redis.pcall("HSET", key, "curr_permits", local_curr_permits)
    end
    return result
end