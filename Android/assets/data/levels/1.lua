function onCreate()
    ------------------
    wall_3 = luajava.new(Wall)
    wall_3:setSpriteAndBodyBox(120.0, 40.0)
    wall_3:setPosition(42.0, 170.0)
    wall_3:setRotation(0.0)
    wall_3:setBodyType(BodyType.StaticBody)
    wall_3:setName('wall_3')
    stage:addActor(wall_3)

    ------------------
    player = luajava.new(Player)
    player:setPosition(3.6842613, 248.89833)
    player:setRotation(0.48223525)
    player:setBodyType(BodyType.DynamicBody)
    player:setName('player')
    stage:addActor(player)

    ------------------
    coin_0 = luajava.new(Coin)
    coin_0:setPosition(48.00003, 239.99994)
    coin_0:setRotation(0.0)
    coin_0:setBodyType(BodyType.KinematicBody)
    coin_0:setName('coin_0')
    stage:addActor(coin_0)

    ------------------
    wall_0 = luajava.new(Wall)
    wall_0:setSpriteAndBodyBox(40.0, 40.0)
    wall_0:setPosition(182.0, 127.99996)
    wall_0:setRotation(90.0)
    wall_0:setBodyType(BodyType.StaticBody)
    wall_0:setName('wall_0')
    stage:addActor(wall_0)

    ------------------
    wall_1 = luajava.new(Wall)
    wall_1:setSpriteAndBodyBox(120.0, 40.0)
    wall_1:setPosition(317.99994, 148.0)
    wall_1:setRotation(15.0)
    wall_1:setBodyType(BodyType.StaticBody)
    wall_1:setName('wall_1')
    stage:addActor(wall_1)

    ------------------
    coin_2 = luajava.new(Coin)
    coin_2:setPosition(316.00003, 187.99997)
    coin_2:setRotation(0.0)
    coin_2:setBodyType(BodyType.KinematicBody)
    coin_2:setName('coin_2')
    stage:addActor(coin_2)

    ------------------
    coin_3 = luajava.new(Coin)
    coin_3:setPosition(182.00003, 159.99998)
    coin_3:setRotation(0.0)
    coin_3:setBodyType(BodyType.KinematicBody)
    coin_3:setName('coin_3')
    stage:addActor(coin_3)

    ------------------
    wall_4 = luajava.new(Wall)
    wall_4:setSpriteAndBodyBox(120.0, 40.0)
    wall_4:setPosition(477.99994, 127.999985)
    wall_4:setRotation(22.0)
    wall_4:setBodyType(BodyType.KinematicBody)
    wall_4:setName('wall_4')
    stage:addActor(wall_4)

    ------------------
    coin_5 = luajava.new(Coin)
    coin_5:setPosition(472.00003, 157.99998)
    coin_5:setRotation(0.0)
    coin_5:setBodyType(BodyType.KinematicBody)
    coin_5:setName('coin_5')
    stage:addActor(coin_5)

    ------------------
    wall_6 = luajava.new(Wall)
    wall_6:setSpriteAndBodyBox(120.0, 40.0)
    wall_6:setPosition(611.99994, 171.99998)
    wall_6:setRotation(5.0)
    wall_6:setBodyType(BodyType.StaticBody)
    wall_6:setName('wall_6')
    stage:addActor(wall_6)

    ------------------
    coin_7 = luajava.new(Coin)
    coin_7:setPosition(607.0, 222.99995)
    coin_7:setRotation(45.0)
    coin_7:setBodyType(BodyType.KinematicBody)
    coin_7:setName('coin_7')
    stage:addActor(coin_7)

end
function onBeginContact(contact)
    coin = nil
    if contact:getFixtureA():getBody() == player:getBody() and
            stage:getActorByBody(contact:getFixtureB():getBody()):getType() == TYPE.COIN then
        coin = stage:getActorByBody(contact:getFixtureB():getBody())
    end

    if contact:getFixtureA():getBody() == player:getBody() and
            contact:getFixtureB():getBody() == wall_4:getBody()
            or
            contact:getFixtureA():getBody() == wall_4:getBody() and
            contact:getFixtureB():getBody() == player:getBody()
            then
        wall_4:getBody():setLinearVelocity(0, -0.5)
    end

    if coin ~= nil and coin ~= coin_2 then
        stage:safeRemoveActor(coin)
    end

    if coin_2 == coin then
        player:kill()
    end
end
function onEndContact(contact)
end
function onCheck()
    coins = stage:getGameActorsByType(TYPE.COIN)
    return coins:size() == 1
end