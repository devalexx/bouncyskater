/*******************************************************************************
 * Copyright 2013 See AUTHORS file.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE V3
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.alex.bs.models;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class Skate extends SimpleActor {
    private Body leftWheelBody, rightWheelBody;

    public Skate() {
        //sprite = TextureManager.getInstance().getSpriteFromDefaultAtlas("cloud");
        type = SimpleActor.TYPE.SKATE;
        setBodyBox(100, 10);
    }

    @Override
    public void createPhysicsActor(World physicsWorld) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(getPhysicsWidth() / 2, getPhysicsHeight() / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1;
        fixtureDef.filter.categoryBits = CATEGORY_SKATE;
        fixtureDef.filter.maskBits = MASK_SKATE;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = physicsWorld.createBody(bodyDef);
        body.createFixture(fixtureDef);
        body.resetMassData();





        CircleShape polygonShape2 = new CircleShape();
        polygonShape2.setRadius(getPhysicsWidth() / 2 / 10);

        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = polygonShape2;
        fixtureDef2.density = 1;
        fixtureDef2.friction = 10.4f;
        fixtureDef2.filter.categoryBits = CATEGORY_SKATE;
        fixtureDef2.filter.maskBits = MASK_SKATE;

        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.type = BodyDef.BodyType.DynamicBody;
        bodyDef2.position.set(new Vector2(-getPhysicsWidth() / 3, -getPhysicsWidth() / 5));
        leftWheelBody = physicsWorld.createBody(bodyDef2);
        leftWheelBody.createFixture(fixtureDef2);
        leftWheelBody.resetMassData();



        CircleShape polygonShape3 = new CircleShape();
        polygonShape3.setRadius(getPhysicsWidth() / 2 / 10);

        FixtureDef fixtureDef3 = new FixtureDef();
        fixtureDef3.shape = polygonShape3;
        fixtureDef3.density = 1;
        fixtureDef3.friction = 10.4f;
        fixtureDef3.filter.categoryBits = CATEGORY_SKATE;
        fixtureDef3.filter.maskBits = MASK_SKATE;

        BodyDef bodyDef3 = new BodyDef();
        bodyDef3.type = BodyDef.BodyType.DynamicBody;
        bodyDef3.position.set(new Vector2(getPhysicsWidth() / 3 , -getPhysicsWidth() / 5));
        rightWheelBody = physicsWorld.createBody(bodyDef3);
        rightWheelBody.createFixture(fixtureDef3);
        rightWheelBody.resetMassData();



        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.initialize(leftWheelBody, body, leftWheelBody.getWorldCenter());
        Joint jointLeft = physicsWorld.createJoint(jointDef);

        RevoluteJointDef jointDef2 = new RevoluteJointDef();
        jointDef2.initialize(rightWheelBody, body, rightWheelBody.getWorldCenter());
        Joint jointRight = physicsWorld.createJoint(jointDef2);



        polygonShape.dispose();

        super.createPhysicsActor(physicsWorld);
    }

    public void roll(float force) {
        leftWheelBody.applyTorque(force, true);
        rightWheelBody.applyTorque(force, true);
    }
}
