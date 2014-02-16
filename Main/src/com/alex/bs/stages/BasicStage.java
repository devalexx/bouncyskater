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
package com.alex.bs.stages;

import com.alex.bs.models.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.*;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;

import java.io.*;

public abstract class BasicStage extends Stage {
    public static final float WORLD_TO_BOX = 0.01f;
    public static final float BOX_TO_WORLD = 1 / WORLD_TO_BOX;
    protected World physicsWorld;
    protected boolean debug;

    protected BasicStage(float width, float height, boolean keepAspectRatio) {
        super(width, height, keepAspectRatio);
    }

    public World getPhysicsWorld() {
        return physicsWorld;
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);

        if(actor instanceof SimpleActor)
            ((SimpleActor) actor).createPhysicsActor(physicsWorld);
    }

    public boolean isDebug() {
        return debug;
    }

    @Override
    public boolean keyDown(int keyCode) {
        switch(keyCode) {
            case Input.Keys.D:
                debug = !debug;
                break;
        }

        return super.keyDown(keyCode);
    }
}