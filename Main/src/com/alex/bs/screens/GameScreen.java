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
import com.alex.bs.stages.*;
import com.badlogic.gdx.Gdx;

public class GameScreen extends BasicScreen {
    private GameStage world;

    public GameScreen(BSGame game) {
        super(game, new GameStage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        world = (GameStage) stage;
        this.game = game;
    }
}
