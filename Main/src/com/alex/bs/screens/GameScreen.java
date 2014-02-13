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
package com.alex.bs.screens;

import com.alex.bs.BSGame;
import com.alex.bs.stages.GameStage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

public class GameScreen implements Screen {
    private GameStage world;
    private Camera camera;
    private BSGame game;

    public GameScreen(BSGame game) {
        this.game = game;
        world = new GameStage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.input.setInputProcessor(world);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        world.act(delta);
        world.draw();

        if(world.isDebug()) {
            Matrix4 debugMatrix = new Matrix4(camera.combined);
            debugMatrix.scl(GameStage.BOX_TO_WORLD);

            Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
            debugRenderer.render(world.getPhysicsWorld(), debugMatrix);
        }
    }

    @Override
    public void resize(int width, int height) {
        /*camera.viewportWidth = 800;
        camera.viewportHeight = 480;
        camera.position.set(0, -10, 0f);*/
        //world.getSpriteBatch().setProjectionMatrix(camera.projection);
    }

    @Override
    public void show() {
        camera = (OrthographicCamera) world.getCamera();
        camera.viewportHeight = 480;
        camera.viewportWidth = 800;

        camera.position.set(camera.viewportWidth * .5f, camera.viewportHeight * .5f, 0f);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
