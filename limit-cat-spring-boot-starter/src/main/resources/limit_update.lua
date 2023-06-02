local key = KEYS[1]
local exprie = ARGV[1]

-- 更新流控计数
local currentValue = tonumber(redis.pcall("INCRBY", key, 1))
if currentValue == 1 then
    redis.pcall("EXPIRE", key, exprie)
end
return currentValue