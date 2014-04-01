function onCreate()
    ------------------
    mesh_3 = luajava.new(Mesh)
    mesh_3:addVertex(359.99997, 72.0)
    mesh_3:addVertex(239.99988, 36.000122)
    mesh_3:addVertex(147.99997, 20.0)
    mesh_3:addVertex(75.99995, 9.1552734E-5)
    mesh_3:addVertex(7.999916, -11.9999695)
    mesh_3:addVertex(-88.00009, -27.999939)
    mesh_3:addVertex(-172.0, -35.99994)
    mesh_3:addVertex(-248.00003, -43.99994)
    mesh_3:addVertex(-304.0001, -55.99997)
    mesh_3:addVertex(-360.0, -71.99997)
    mesh_3:addVertex(328.0, -48.0)
    mesh_3:addVertex(343.99997, -48.0)
    mesh_3:setPosition(-17.999939, 435.99994)
    mesh_3:setRotation(0.0)
    mesh_3:setBodyType(BodyType.StaticBody)
    mesh_3:setName('mesh_3')
    stage:addActor(mesh_3)

    ------------------
    mesh_4 = luajava.new(Mesh)
    mesh_4:addVertex(-201.99991, 56.00003)
    mesh_4:addVertex(-272.0, -63.99997)
    mesh_4:addVertex(-164.0, -76.00003)
    mesh_4:addVertex(31.999878, -76.00003)
    mesh_4:addVertex(152.0, -55.99997)
    mesh_4:addVertex(260.0, 12.0000305)
    mesh_4:addVertex(272.00006, 64.00003)
    mesh_4:addVertex(207.99994, 76.00003)
    mesh_4:addVertex(83.99994, 60.00009)
    mesh_4:setPosition(591.9998, 469.99994)
    mesh_4:setRotation(4.0)
    mesh_4:setBodyType(BodyType.KinematicBody)
    mesh_4:setName('mesh_4')
    stage:addActor(mesh_4)

    ------------------
    wall_0 = luajava.new(Wall)
    wall_0:setSpriteAndBodyBox(400.0, 30.0)
    wall_0:setPosition(-130.0, 397.99994)
    wall_0:setRotation(0.0)
    wall_0:setBodyType(BodyType.StaticBody)
    wall_0:setName('wall_0')
    stage:addActor(wall_0)

    ------------------
    player = luajava.new(Player)
    player:setPosition(-227.99997, 464.99988)
    player:setRotation(0.0)
    player:setBodyType(BodyType.DynamicBody)
    player:setName('player')
    stage:addActor(player)

    ------------------
    skate_1 = luajava.new(Skate)
    skate_1:setPosition(-144.00002, 440.0)
    skate_1:setRotation(0.0)
    skate_1:setBodyType(BodyType.DynamicBody)
    skate_1:setName('skate_1')
    stage:addActor(skate_1)

    ------------------
    coin_2 = luajava.new(Coin)
    coin_2:setPosition(-27.999878, 459.99994)
    coin_2:setRotation(0.0)
    coin_2:setBodyType(BodyType.KinematicBody)
    coin_2:setName('coin_2')
    stage:addActor(coin_2)

    ------------------
    coin_3 = luajava.new(Coin)
    coin_3:setPosition(268.00006, 523.99994)
    coin_3:setRotation(0.0)
    coin_3:setBodyType(BodyType.KinematicBody)
    coin_3:setName('coin_3')
    stage:addActor(coin_3)

    ------------------
    coin_4 = luajava.new(Coin)
    coin_4:setPosition(668.0, 551.99994)
    coin_4:setRotation(0.0)
    coin_4:setBodyType(BodyType.KinematicBody)
    coin_4:setName('coin_4')
    stage:addActor(coin_4)

end
function onBeginContact(contact)
    if player:getPosition().x > 150 then
        mesh_4:getBody():setLinearVelocity(1, 0)
    end

    if contact:getFixtureA():getBody() == player:getBody() and
            stage:getActorByBody(contact:getFixtureB():getBody()):getType() == TYPE.COIN then
        stage:safeRemoveActor(stage:getActorByBody(contact:getFixtureB():getBody()))
    end
end

function onEndContact(contact)
end
function onEndContact(contact)
end
function onCheck()
    coins = stage:getGameActorsByType(TYPE.COIN)
    return coins:size() == 0
end