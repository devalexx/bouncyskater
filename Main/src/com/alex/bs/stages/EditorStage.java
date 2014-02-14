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

public class EditorStage extends BasicStage {

    public EditorStage(float width, float height) {
        super(width, height, true);

        physicsWorld = new World(new Vector2(0, 0), true);

        Skate skate = new Skate();
        addActor(skate);

        Wall wall = new Wall();
        wall.setPosition(new Vector2(0, -50));
        addActor(wall);
        wall = new Wall();
        wall.setPosition(new Vector2(-200, -50));
        wall.setRotation(-10);
        addActor(wall);
        wall = new Wall();
        wall.setPosition(new Vector2(400, -10));
        wall.setRotation(0);
        addActor(wall);

        Player player = new Player();
        player.setPosition(new Vector2(100, 50));
        addActor(player);

        getCamera().position.set(0, 0, 0f);
    }

    @Override
    public void act(float delta) {
        getCamera().update();
        getSpriteBatch().setProjectionMatrix(getCamera().projection);

        physicsWorld.step(1 / 60f, 8, 3);

        super.act(delta);
    }

    @Override
    public boolean keyDown(int keyCode) {
        switch(keyCode) {
            case Input.Keys.SPACE:
                break;
            case Input.Keys.UP:
                getCamera().position.add(0, 10, 0);
                break;
            case Input.Keys.DOWN:
                getCamera().position.add(0, -10, 0);
                break;
            case Input.Keys.LEFT:
                getCamera().position.add(-10, 0, 0);
                break;
            case Input.Keys.RIGHT:
                getCamera().position.add(10, 0, 0);
                break;
        }

        return super.keyDown(keyCode);
    }
}
