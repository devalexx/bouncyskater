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
package com.alex.bs.managers;


import com.alex.bs.models.SimpleActor;
import com.badlogic.gdx.scenes.scene2d.*;

public class ExportManager {
    private Stage stage;

    public ExportManager(Stage stage) {
        this.stage = stage;
    }

    public String export() {
        String accum = "function onCreate()\n";

        for(Actor a : stage.getRoot().getChildren()) {
            if(!(a instanceof SimpleActor))
                continue;

            SimpleActor sa = (SimpleActor) a;
            accum += exportActor(sa);
        }

        accum += "end\n" +
                "\n" +
                "function onCheck()\n" +
                "    return false\n" +
                "end";

        return accum;
    }

    public String exportActor(SimpleActor sa) {
        String s = "";
        switch(sa.getType()) {
            case WALL:
                s += "    obj = luajava.new(Wall)\n" +
                        "    obj:setSpriteAndBodyBox(" + sa.getWidth() + ", " + sa.getHeight() + ")\n";
                break;
            case PLAYER:
                s += "    obj = luajava.new(Player)\n";
                break;
            case SKATE:
                s += "    obj = luajava.new(Skate)\n";
                break;
        }

        if(s.isEmpty())
            return "    --Error import " + sa + "\n\n";
        else {
            s += "    obj:setPosition(" + (sa.getX() + sa.getWidth() / 2) + ", " +
                    (sa.getY() + sa.getHeight() / 2) + ")\n";
            s += "    obj:setRotation(" + sa.getRotation() + ")\n";
            if(sa.getName() != null)
                s += "    obj:setName('" + sa.getName() + "')\n\n";
            s += "    stage:addActor(obj)\n\n";
            return s;
        }
    }
}
