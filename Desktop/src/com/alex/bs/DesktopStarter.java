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
package com.alex.bs;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopStarter {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Bouncy Skate";
        cfg.useGL20 = true;
        //cfg.height = 240; cfg.width = 320;
        //cfg.height = 320; cfg.width = 480;
        //cfg.height = 480; cfg.width = 640;
        cfg.height = 480; cfg.width = 800;
        //cfg.height = 480; cfg.width = 854;
        //cfg.height = 640; cfg.width = 960;
        //cfg.height = 600; cfg.width = 1024;
        //cfg.height = 768; cfg.width = 1024;
        //cfg.height = 768; cfg.width = 1280;

        new LwjglApplication(new BSGame(), cfg);
    }
}
