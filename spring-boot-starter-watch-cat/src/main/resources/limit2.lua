--- 获取令牌 2021-11-23 18:11:09
--- 返回码
--- 0 没有令牌桶配置
--- -1 表示取令牌失败，也就是桶里没有令牌
--- 1 表示取令牌成功
--- @param key 令牌的唯一标识
--- @param permits  请求令牌数量
--- @param curr_mill_second 当前时间
--- @param context 使用令牌的应用标识
local function acquire(key, permits, curr_mill_second, context)
    --- 令牌桶内数据：
    --- last_mill_second  最后一次放入令牌时间
    --- curr_permits  当前桶内令牌数
    --- max_permits   桶内令牌最大数量（最大令牌数 也就是并发数，比如10万）
    --- rate  令牌放置速度
    --- app数量
    local rate_limit_info = redis.pcall("HMGET", key, "last_mill_second", "curr_permits", "max_permits", "rate", "apps")
    local last_mill_second = rate_limit_info[1]
    local curr_permits = tonumber(rate_limit_info[2])
    local max_permits = tonumber(rate_limit_info[3])
    local rate = rate_limit_info[4]
    local apps = rate_limit_info[5]

    --- 标识没有配置令牌桶
    if type(apps) == 'boolean' or apps == nil or not contains(apps, context) then
        return 0
    end

    --- 设置一个变量为当前令牌数，默认为最大值
    local local_curr_permits = max_permits;
    --- 令牌桶刚刚创建，上一次获取令牌的毫秒数为空
    --- 根据和上一次向桶里添加令牌的时间和当前时间差，触发式往桶里添加令牌，并且更新上一次向桶里添加令牌的时间
    --- 如果向桶里添加的令牌数不足一个，则不更新上一次向桶里添加令牌的时间
    --- ~=	不等于，检测两个值是否相等，不相等返回 true，否则返回 false	(A ~= B) 为 true。
    if (type(last_mill_second) ~= 'boolean' and last_mill_second ~= false and last_mill_second ~= nil) then
        --- 非首次添加
        --- 计算应该添加的令牌数
        local reverse_permits = math.floor(((curr_mill_second - last_mill_second) / 1000) * rate)
        local expect_curr_permits = reverse_permits + curr_permits;
        --- 设置当前令牌数 为新增令牌后的值，和最大值之间取最小。（因为令牌可能被消费）
        local_curr_permits = math.min(expect_curr_permits, max_permits);
        --- 大于0表示不是第一次获取令牌，也没有向桶里添加令牌
        if (reverse_permits > 0) then
            redis.pcall("HSET", key, "last_mill_second", curr_mill_second)
        end
    else
        --- 首次获取令牌，不会新增令牌，设置上次添加令牌的毫秒数
        redis.pcall("HSET", key, "last_mill_second", curr_mill_second)
    end
    local result = -1
    if (local_curr_permits - permits >= 0) then
        result = 1
        redis.pcall("HSET", key, "curr_permits", local_curr_permits - permits)
    else
        redis.pcall("HSET", key, "curr_permits", local_curr_permits)
    end
    return result
end