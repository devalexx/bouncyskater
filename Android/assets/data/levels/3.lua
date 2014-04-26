function addObjects()
    ------------------
    wall_0 = luajava.new(Wall)
    wall_0:setSpriteAndBodyBox(400.0, 20.0)
    wall_0:setPosition(332.0, 148.99997)
    wall_0:setRotation(0.0)
    wall_0:setBodyType(BodyType.StaticBody)
    wall_0:setName('wall_0')
    wall_0:setVisible(true)
    stage:addActor(wall_0)

    ------------------
    mesh_1 = luajava.new(Mesh)
    mesh_1:addVertex(118.00003, 59.99994)
    mesh_1:addVertex(126.0, -68.0)
    mesh_1:addVertex(146.0, -68.0)
    mesh_1:addVertex(130.0, 103.99997)
    mesh_1:addVertex(34.0, 103.99997)
    mesh_1:addVertex(-146.0, 72.0)
    mesh_1:addVertex(-134.0, -103.99997)
    mesh_1:addVertex(-94.0, -88.0)
    mesh_1:addVertex(-102.0, 27.99997)
    mesh_1:addVertex(-5.9999695, 59.99994)
    mesh_1:addVertex(62.00003, 76.00003)
    mesh_1:setPosition(323.99994,902.25)
    mesh_1:setRotation(-10.0)
    mesh_1:setBodyType(BodyType.DynamicBody)
    mesh_1:setName('mesh_1')
    mesh_1:setVisible(true)
    stage:addActor(mesh_1)

    ------------------
    player = luajava.new(Player)
    player:setPosition(293.99994, 215.89993)
    player:setRotation(0.0)
    player:setBodyType(BodyType.DynamicBody)
    player:setName('player')
    player:setVisible(true)
    stage:addActor(player)

    ------------------
    wall_3 = luajava.new(Wall)
    wall_3:setSpriteAndBodyBox(50.0, 20.0)
    wall_3:setPosition(575.9999, 158.24991)
    wall_3:setRotation(0.0)
    wall_3:setBodyType(BodyType.StaticBody)
    wall_3:setName('wall_3')
    wall_3:setVisible(false)
    stage:addActor(wall_3)

    ------------------
    wall_4 = luajava.new(Wall)
    wall_4:setSpriteAndBodyBox(400.0, 20.0)
    wall_4:setPosition(1076.0, 146.24997)
    wall_4:setRotation(0.0)
    wall_4:setBodyType(BodyType.StaticBody)
    wall_4:setName('wall_4')
    wall_4:setVisible(true)
    stage:addActor(wall_4)

    ------------------
    wall_5 = luajava.new(Wall)
    wall_5:setSpriteAndBodyBox(50.0, 20.0)
    wall_5:setPosition(657.99994, 194.24995)
    wall_5:setRotation(0.0)
    wall_5:setBodyType(BodyType.StaticBody)
    wall_5:setName('wall_5')
    wall_5:setVisible(false)
    stage:addActor(wall_5)

    ------------------
    wall_6 = luajava.new(Wall)
    wall_6:setSpriteAndBodyBox(50.0, 20.0)
    wall_6:setPosition(726.0, 140.24997)
    wall_6:setRotation(0.0)
    wall_6:setBodyType(BodyType.StaticBody)
    wall_6:setName('wall_6')
    wall_6:setVisible(false)
    stage:addActor(wall_6)

    ------------------
    wall_7 = luajava.new(Wall)
    wall_7:setSpriteAndBodyBox(50.0, 20.0)
    wall_7:setPosition(813.99994, 176.24997)
    wall_7:setRotation(0.0)
    wall_7:setBodyType(BodyType.StaticBody)
    wall_7:setName('wall_7')
    wall_7:setVisible(false)
    stage:addActor(wall_7)

    ------------------
    coin_0 = luajava.new(Coin)
    coin_0:setPosition(1092.0, 191.99994)
    coin_0:setRotation(0.0)
    coin_0:setBodyType(BodyType.KinematicBody)
    coin_0:setName('coin_0')
    coin_0:setVisible(true)
    stage:addActor(coin_0)

end
function onCreate()
    addObjects()
end
function onBeginContact(contact)
    coin = nil
    if contact:getFixtureA():getBody() == player:getBody() and
            stage:getActorByBody(contact:getFixtureB():getBody()):getType() == TYPE.COIN then
        coin = stage:getActorByBody(contact:getFixtureB():getBody())
    end

    if coin ~= nil then
        stage:safeRemoveActor(coin)
    end
end
function onEndContact(contact)
end
function onCheck()
    coins = stage:getGameActorsByType(TYPE.COIN)
    if coins:size() == 0 then
        return 1
    end

    return 0
end