function onCreate()
    wall = luajava.new(Wall)
    wall:setSpriteAndBodyBox(10, 300)
    wall:setPosition(-400, 0)
    print(stage)
    stage:addActor(wall)
end

function onCheck()
    return false
end