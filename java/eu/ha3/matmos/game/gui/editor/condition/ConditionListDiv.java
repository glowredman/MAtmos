package eu.ha3.matmos.game.gui.editor.condition;

import com.google.common.base.Optional;
import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.engine.condition.ConditionSet;
import eu.ha3.matmos.game.MCGame;
import eu.ha3.matmos.game.gui.editor.div.Div;
import eu.ha3.matmos.game.gui.editor.element.Button;
import eu.ha3.matmos.game.gui.editor.element.Scrollbar;
import org.lwjgl.input.Keyboard;

import java.util.*;

/**
 * @author dags_ <dags@dags.me>
 */

public class ConditionListDiv extends Div
{
    private final Map<String, ConditionSet> conditionSets = new HashMap<String, ConditionSet>();
    private final ConditionEditorDiv editorDiv;
    private final Button createButton = new Button().setMarginTop(6).setDisplayString("Create New").setBackgroundColor(0xFF333333);
    private final Scrollbar scrollbar = new Scrollbar();

    private String selected = "";
    private int left = 0;
    private int right = 0;
    private int top = 0;
    private int bottom = 0;

    private int contentHeight = 0;

    public ConditionListDiv(ConditionEditorDiv editor, float width, float height, float marginLeft, float marginTop)
    {
        super(width, height, marginLeft, marginTop);
        editorDiv = editor;
        conditionSets.putAll(MAtmos.dataRegistry.conditions);
    }

    @Override
    public void onDraw(int mouseX, int mouseY, int left, int top, int right, int bottom)
    {
        createButton.setDims(right - left, 20);
        createButton.draw(left, top);

        top += 20;

        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;

        if (contentHeight > bottom)
        {
            scrollbar.draw(mouseX, mouseY, right, top, bottom);
            top -= scrollbar.getContentTop(contentHeight, this.top, this.bottom);
        }

        contentHeight = this.top;
        List<String> conditions = new ArrayList<String>(conditionSets.keySet());
        Collections.sort(conditions);
        for (String s : conditions)
        {
            MCGame.drawString(s, left + 5, top + 6, 0xFFFFFFFF);
            drawHorizontalLine(left + 1, right - 2, top + ConditionEditorDiv.lineHeight, 0xAAFFFFFF);
            if (s.equals(selected))
            {
                drawRect(left, top, right, top + ConditionEditorDiv.lineHeight, 0x33FFFFFF);
            }
            else if (mouseOver(mouseX, mouseY, left, top, right, top + ConditionEditorDiv.lineHeight))
            {
                drawRect(left, top, right, top + ConditionEditorDiv.lineHeight, 0x22FFFFFF);
            }
            top += (ConditionEditorDiv.lineHeight + 1);
            contentHeight += (ConditionEditorDiv.lineHeight + 1);
        }
        contentHeight += (ConditionEditorDiv.lineHeight + 1);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int btn)
    {
        scrollbar.onClick(mouseX, mouseY, right);
        if (createButton.onClick(mouseX, mouseY, left, top - 20))
        {
            editorDiv.newConditionSet();
            return;
        }
        if (mouseOver(mouseX, mouseY, left, top, right, bottom))
        {
            int top = this.top;
            List<String> conditions = new ArrayList<String>(conditionSets.keySet());
            Collections.sort(conditions);
            for (String s : conditions)
            {
                if (mouseOver(mouseX, mouseY, left, top, right, top + ConditionEditorDiv.lineHeight))
                {
                    if (!s.equals(selected))
                    {
                        selected = s;
                        editorDiv.setConditionSet(conditionSets.get(s));
                    }
                    return;
                }
                top += (ConditionEditorDiv.lineHeight + 1);
            }
            selected = "";
            Optional<ConditionSet> optional = editorDiv.clearCurrent();
            if (optional.isPresent())
            {
                ConditionSet set = optional.get();
                conditionSets.put(set.getName(), set);
            }
        }
    }

    @Override
    public void onMouseRelease()
    {
        scrollbar.onMouseRelease();
        createButton.onRelease();
    }

    @Override
    public void onKeyType(char c, int code)
    {
        if (!editorDiv.active())
        {
            if (code == Keyboard.KEY_DELETE && !"".equals(selected) && conditionSets.containsKey(selected))
            {
                conditionSets.remove(selected);
                editorDiv.clearCurrent();
                selected = "";
            }
            else if (code == Keyboard.KEY_ESCAPE)
            {
                selected = "";
                Optional<ConditionSet> optional = editorDiv.clearCurrent();
                if (optional.isPresent())
                {
                    conditionSets.put(optional.get().getName(), optional.get());
                }
            }
        }
    }
}
