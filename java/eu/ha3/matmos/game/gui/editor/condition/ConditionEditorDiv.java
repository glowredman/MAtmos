package eu.ha3.matmos.game.gui.editor.condition;

import com.google.common.base.Optional;
import eu.ha3.matmos.engine.condition.Checkable;
import eu.ha3.matmos.engine.condition.ConditionSet;
import eu.ha3.matmos.game.MCGame;
import eu.ha3.matmos.game.gui.editor.div.Div;
import eu.ha3.matmos.util.Timer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public class ConditionEditorDiv extends Div
{
    private final List<ConditionField> lines = new ArrayList<ConditionField>();
    private final Timer timer = new Timer();

    protected static int lineHeight = 20;

    private String conditionSetName = "";

    private int editorTop = 0;
    private int editorLeft = 0;
    private int editorRight = 0;
    private int editorBottom = 0;

    private boolean cursor = false;

    public ConditionEditorDiv(float w, float h, float ml, float mt)
    {
        super(w, h, ml, mt);
    }

    public Optional<ConditionSet> clearCurrent()
    {
        if (conditionSetName.length() == 0)
        {
            return Optional.absent();
        }
        ConditionSet result = new ConditionSet(conditionSetName);
        for (ConditionField cb : lines)
        {
            if (cb.valid())
            {
                result.addCondition(cb.get());
            }
        }
        conditionSetName = "Nothing Selected";
        lines.clear();
        return Optional.of(result);
    }

    public ConditionEditorDiv setConditionSet(ConditionSet set)
    {
        lines.clear();
        conditionSetName = set.getName();
        for (Checkable c : set.getConditions())
        {
            lines.add(new ConditionField(c.serialize()));
        }
        return this;
    }

    @Override
    public void onDraw(int mouseX, int mouseY, int left, int top, int right, int bottom)
    {
        drawBorderedBox(left, top, right, top + lineHeight, 0xFF333333, 0xFFFFFFFF);
        MCGame.drawString(conditionSetName, left + 5, top + 6, 0xFFFFFFFF);
        drawBorderedBox(left, top += 20, right, bottom, 0xDD666666, 0xFFFFFFFF);

        editorTop = top + 1;
        editorLeft = left + 1;
        editorRight = right - 1;
        editorBottom = bottom;

        List<ConditionField> empty = new ArrayList<ConditionField>();
        for (ConditionField cf : lines)
        {
            if (cf.empty() && !cf.active())
            {
                empty.add(cf);
            }
            else
            {
                if (timer.getPeriodMs() > 500)
                {
                    timer.punchIn();
                    cursor = !cursor;
                }
                cf.setHovered(mouseOver(mouseX, mouseY, editorLeft, top, editorRight, top + lineHeight));
                cf.drawBox(editorLeft, top, editorRight, top + lineHeight);
                cf.draw(cursor, left + 5, top + 6, editorRight);
                drawHorizontalLine(editorLeft + 1, editorRight - 2, top + lineHeight, 0xAAFFFFFF);
                top += (lineHeight + 1);
            }
        }
        lines.removeAll(empty);
        timer.punchOut();
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button)
    {
        if (mouseOver(mouseX, mouseY, editorLeft, editorTop, editorRight, editorBottom))
        {
            boolean noneSelected = true;
            for (ConditionField cf : lines)
            {
                if (cf.hovered())
                {
                    cf.setActive(noneSelected);
                    noneSelected = false;
                }
                else if (cf.active())
                {
                    cf.setActive(false);
                }
            }
            if (noneSelected)
            {
                ConditionField cf = new ConditionField();
                cf.setActive(true);
                lines.add(cf);
            }
        }
        else
        {
            for (ConditionField cf : lines)
                cf.setActive(false);
        }
    }

    @Override
    public void onKeyType(char c, int code)
    {
        for (ConditionField cf : lines)
        {
            if (cf.active())
            {
                cf.onKeyType(c, code);
                return;
            }
        }
    }
}
