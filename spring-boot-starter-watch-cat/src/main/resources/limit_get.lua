local key = KEYS[1]
local limit = tonumber(ARGV[1])

-- 检查是否触发流控
local currentValue = tonumber(redis.pcall('get', key) or "0")
if currentValue + 1 > limit then
    return 0
else
    return 1
end