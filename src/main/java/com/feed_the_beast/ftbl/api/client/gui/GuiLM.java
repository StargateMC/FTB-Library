package com.feed_the_beast.ftbl.api.client.gui;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.client.gui.widgets.PanelLM;
import com.feed_the_beast.ftbl.api.client.gui.widgets.WidgetLM;
import com.feed_the_beast.ftbl.util.TextureCoords;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LatvianModder on 09.06.2016.
 */
@SideOnly(Side.CLIENT)
public abstract class GuiLM extends PanelLM
{
    private static final List<String> tempTextList = new ArrayList<>();

    public final Minecraft mc;
    public final FontRenderer font;
    public ScaledResolution screen;
    public float partialTicks;
    public int mouseX, mouseY, pmouseX, pmouseY, dmouseX, dmouseY;
    public int mouseWheel, dmouseWheel;
    private boolean refreshWidgets;

    public GuiLM()
    {
        super(0, 0, 300, 200);
        mc = FTBLibClient.mc();
        font = createFont(mc);
    }

    public static void setupDrawing()
    {
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();
    }

    public static void drawTexturedRect(double x, double y, double w, double h, double u0, double v0, double u1, double v1)
    {
        if(u0 == 0D && v0 == 0D && u1 == 0D && v1 == 0D)
        {
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buffer = tessellator.getBuffer();
            buffer.begin(7, DefaultVertexFormats.POSITION);
            buffer.pos(x, y + h, 0D).endVertex();
            buffer.pos(x + w, y + h, 0D).endVertex();
            buffer.pos(x + w, y, 0D).endVertex();
            buffer.pos(x, y, 0D).endVertex();
            tessellator.draw();
        }
        else
        {
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buffer = tessellator.getBuffer();
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(x, y + h, 0D).tex(u0, v1).endVertex();
            buffer.pos(x + w, y + h, 0D).tex(u1, v1).endVertex();
            buffer.pos(x + w, y, 0D).tex(u1, v0).endVertex();
            buffer.pos(x, y, 0D).tex(u0, v0).endVertex();
            tessellator.draw();
        }
    }

    public static void drawPlayerHead(String username, double x, double y, double w, double h)
    {
        FTBLibClient.setTexture(FTBLibClient.getSkinTexture(username));
        drawTexturedRect(x, y, w, h, 0.125D, 0.125D, 0.25D, 0.25D);
        drawTexturedRect(x, y, w, h, 0.625D, 0.125D, 0.75D, 0.25D);
    }

    public static void drawBlankRect(double x, double y, double w, double h)
    {
        GlStateManager.disableTexture2D();
        drawTexturedRect(x, y, w, h, 0D, 0D, 0D, 0D);
        GlStateManager.enableTexture2D();
    }

    public static void render(TextureCoords tc, double x, double y, double w, double h)
    {
        if(tc != null && tc.isValid())
        {
            FTBLibClient.setTexture(tc.texture);
            drawTexturedRect(x, y, w, h, tc.minU, tc.minV, tc.maxU, tc.maxV);
        }
    }

    public static void drawCenteredString(FontRenderer font, String txt, double x, double y, int color)
    {
        font.drawString(txt, (int) (x - font.getStringWidth(txt) / 2D), (int) (y - font.FONT_HEIGHT / 2D), color);
    }

    public final void initGui()
    {
        screen = new ScaledResolution(mc);
        onInit();
        posX = (screen.getScaledWidth_double() - width) / 2D;
        posY = (screen.getScaledHeight_double() - height) / 2D;
        refreshWidgets();
    }

    public void onInit()
    {
    }

    public final void closeGui()
    {
        onClosed();
        mc.thePlayer.closeScreen();
    }

    public boolean onClosedByKey()
    {
        return true;
    }

    public void onClosed()
    {
    }

    protected FontRenderer createFont(Minecraft mc)
    {
        return mc.fontRendererObj;
    }

    @Override
    public final void refreshWidgets()
    {
        refreshWidgets = true;
    }

    public void setFullscreen()
    {
        width = screen.getScaledWidth_double();
        height = screen.getScaledHeight_double();
        posX = posY = 0D;
    }

    public final void updateGui(int mx, int my, float pt)
    {
        partialTicks = pt;

        pmouseX = mouseX;
        pmouseY = mouseY;

        mouseX = mx;
        mouseY = my;

        dmouseX = mouseX - pmouseX;
        dmouseY = mouseY - pmouseY;

        dmouseWheel = Mouse.getDWheel();
        mouseWheel += dmouseWheel;

        if(refreshWidgets)
        {
            super.refreshWidgets();
            refreshWidgets = false;
        }
    }

    @Override
    public final void renderWidget(GuiLM gui)
    {
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        drawBackground();
        renderWidgets();
    }

    public final void renderWidgets()
    {
        super.renderWidget(this);
    }

    @Override
    public boolean isInside(WidgetLM w)
    {
        double a = w.getAY();

        if(a < -w.height || a > screen.getScaledHeight_double())
        {
            return false;
        }

        a = w.getAX();

        return a >= -w.width && a <= screen.getScaledWidth_double();
    }

    public boolean drawDefaultBackground()
    {
        return true;
    }

    public void drawBackground()
    {
    }

    public void drawForeground()
    {
        addMouseOverText(this, tempTextList);
        GuiUtils.drawHoveringText(tempTextList, mouseX, Math.max(mouseY, 18), screen.getScaledWidth(), screen.getScaledHeight(), 0, font);
        tempTextList.clear();
    }

    public final boolean isMouseOver(WidgetLM w)
    {
        double ax = w.getAX();
        double ay = w.getAY();
        return mouseX >= ax && mouseY >= ay && mouseX < ax + w.width && mouseY < ay + w.height;
    }

    public GuiScreen getWrapper()
    {
        return new GuiWrapper(this);
    }

    public final void openGui()
    {
        mc.displayGuiScreen(getWrapper());
    }

    public final void playSoundFX(SoundEvent e, float pitch)
    {
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(e, pitch));
    }

    public final void scissor(double x, double y, double w, double h)
    {
        int scale = screen.getScaleFactor();
        GL11.glScissor((int) (x * scale), (int) ((screen.getScaledHeight() - y - h) * scale), (int) (w * scale), (int) (h * scale));
    }
}