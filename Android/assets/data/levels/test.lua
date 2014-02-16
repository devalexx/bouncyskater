function onCreate()
    wall = luajava.new(Wall)
    wall:setSpriteAndBodyBox(10, 300)
    wall:setPosition(-400, 0)
    print(stage)
end

function onCheck()
    return false
end