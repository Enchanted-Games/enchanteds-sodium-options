package games.enchanted.enchanteds_sodium_options.common.gui.widget.scroll;

import net.caffeinemc.mods.sodium.client.gui.ColorTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class VideoOptionsList extends VerticalScrollContainerWidget<VideoOptionsList.Entry> {
    public static final Identifier LIST_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/menu_list_background.png");
    public static final Identifier INWORLD_LIST_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");
    public static final int LIST_BACKGROUND_TEXTURE_SIZE = 32;

    public static final int DEFAULT_CHILD_HEIGHT = 25;
    public static final int DEFAULT_CHILD_WIDTH = 150;
    public static final int ROW_WIDTH = 310;

    @Nullable private OptionEntry lastEntry = null;

    public VideoOptionsList(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> visitor) {
        this.children().forEach(child -> child.widgetChildren().forEach(visitor));
    }


    public WidgetPosition addOption(AbstractWidget child, ModInfo modInfo) {
        if(this.lastEntry != null && this.lastEntry instanceof OptionEntry optionEntry) {
            optionEntry.setSecondChild(child);
            this.lastEntry = null;
            return new WidgetPosition(this.children().size() - 1, true);
        }
        child.setWidth(DEFAULT_CHILD_WIDTH);
        OptionEntry entry = new OptionEntry(child, modInfo);
        this.lastEntry = entry;
        this.addChild(entry);
        return new WidgetPosition(this.children().size() - 1, false);
    }

    public WidgetPosition addBigOption(AbstractWidget child, ModInfo modInfo) {
        this.lastEntry = null;
        child.setWidth(ROW_WIDTH);
        this.addChild(new OptionEntry(child, modInfo));
        return new WidgetPosition(this.children().size() - 1, false);
    }

    public void addHeader(Component header, ModInfo modInfo) {
        trySetLastInCategoryOnBottomEntry(modInfo);
        this.lastEntry = null;
        this.addChild(new HeaderEntry(header, modInfo));
    }

    public void addModTitle(Component name, Component version, @Nullable Identifier icon, boolean monochromeIcon, ModInfo modInfo) {
        trySetLastInCategoryOnBottomEntry(modInfo);
        this.lastEntry = null;
        this.addChild(new ModTitleEntry(name, version, icon, monochromeIcon, modInfo));
    }

    public void trySetLastInCategoryOnBottomEntry(ModInfo info) {
        if(this.children().isEmpty()) return;
        if(this.children().getLast() instanceof OptionEntry entry && !entry.id.equals(info.id())) {
            entry.lastInCategory = true;
        }
    }


    @Override
    public int getRowWidth() {
        return ROW_WIDTH;
    }

    @Override
    public int getRowLeft() {
        return this.getMiddleX() - (ROW_WIDTH / 2);
    }

    @Override
    public int getRowRight() {
        return this.getMiddleX() + (ROW_WIDTH / 2);
    }

    private int getMiddleX() {
        return this.getX() + (this.getWidth() / 2);
    }

    @Override
    protected int scrollBarX() {
        return getRowRight() + 8;
    }

    @Override
    protected void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean notInWorld = Minecraft.getInstance().level == null;

        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED,
            notInWorld ? LIST_BACKGROUND : INWORLD_LIST_BACKGROUND,
            this.getX(),
            this.getY(),
            this.getRight(),
            this.getBottom() + (int)this.scrollAmount(),
            this.getWidth(),
            this.getHeight(),
            LIST_BACKGROUND_TEXTURE_SIZE,
            LIST_BACKGROUND_TEXTURE_SIZE
        );

        int separatorTextureWidth = 32;
        int separatorTextureHeight = 2;
        int separatorHeight = 2;

        Identifier headerSeparator = notInWorld ? Screen.HEADER_SEPARATOR : Screen.INWORLD_HEADER_SEPARATOR;
        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED,
            headerSeparator,
            this.getX(),
            this.getY() - 2,
            0.0F,
            0.0F,
            this.getWidth(),
            separatorTextureHeight,
            separatorTextureWidth,
            separatorHeight
        );
        Identifier footerSeparator = notInWorld ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;
        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED,
            footerSeparator,
            this.getX(),
            this.getBottom(),
            0.0F,
            0.0F,
            this.getWidth(),
            separatorTextureHeight,
            separatorTextureWidth,
            separatorHeight
        );
    }

    static abstract class Entry extends Child {
        private static final int ACCENT_LEFT_OFFSET = 4;
        private static final int ACCENT_TOPMOST_OFFSET = 5;
        private static final int ACCENT_BOTTOMMOST_OFFSET = 4;

        final int accentColour;
        final String id;

        Entry(ModInfo info) {
            this.accentColour = info.theme().theme;
            this.id = info.id();
        }

        @Override
        public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
            graphics.fill(
                this.getContentX() - ACCENT_LEFT_OFFSET - 1,
                this.accentTop(),
                this.getContentX() - ACCENT_LEFT_OFFSET,
                this.accentBottom(),
                this.accentColour
            );
        }

        protected int accentTop() {
            return this.getY();
        }

        protected int accentBottom() {
            return this.getY() + this.getHeight();
        }

        @Override
        protected int height() {
            return DEFAULT_CHILD_HEIGHT;
        }
    }

    static class OptionEntry extends Entry {
        final AbstractWidget child;
        @Nullable AbstractWidget secondChild;
        boolean lastInCategory = false;

        OptionEntry(AbstractWidget widget, ModInfo info) {
            super(info);
            setMargins(new Margin(0, 0));
            this.child = widget;
        }

        void setSecondChild(AbstractWidget child) {
            this.secondChild = child;
        }

        @Override
        public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
            super.renderContent(graphics, mouseX, mouseY, hovered, partialTick);

            this.child.setX(this.getContentX());
            this.child.setY(this.getContentYMiddle() - this.child.getHeight() / 2);
            this.child.render(graphics, mouseX, mouseY, partialTick);

            if(this.secondChild == null) return;
            this.secondChild.setX(this.getContentRight() - this.secondChild.getWidth());
            this.secondChild.setY(this.getContentYMiddle() - this.secondChild.getHeight() / 2);
            this.secondChild.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        protected int accentBottom() {
            if(!this.lastInCategory) return super.accentBottom();
            return super.accentBottom() - Entry.ACCENT_BOTTOMMOST_OFFSET;
        }

        @Override
        public List<? extends AbstractWidget> widgetChildren() {
            if(secondChild != null) return List.of(child, secondChild);
            return List.of(child);
        }

        @Override
        public List<? extends NarratableEntry> narratableChildren() {
            if(secondChild != null) return List.of(child, secondChild);
            return List.of(child);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            if(secondChild != null) return List.of(child, secondChild);
            return List.of(child);
        }
    }

    static class HeaderEntry extends Entry {
        private static final Margin HEADER_MARGINS = new Margin(8, 0, 0, 0);

        final Component title;
        final Font font = Minecraft.getInstance().font;

        HeaderEntry(Component header, ModInfo info) {
            super(info);
            setMargins(HEADER_MARGINS);
            this.title = header;
        }

        @Override
        protected int height() {
            return this.font.lineHeight;
        }

        @Override
        public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
            super.renderContent(graphics, mouseX, mouseY, hovered, partialTick);

            graphics.drawString(this.font, this.title, this.getContentX(), this.getContentY(), -1);
        }

        @Override
        public List<? extends AbstractWidget> widgetChildren() {
            return List.of();
        }

        @Override
        public List<? extends NarratableEntry> narratableChildren() {
            return List.of();
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }
    }

    static class ModTitleEntry extends Entry {
        final Component title;
        final Font font = Minecraft.getInstance().font;
        @Nullable final Identifier icon;
        final Component version;
        final boolean monochromeIcon;
        final int iconColour;

        ModTitleEntry(Component title, Component version, @Nullable Identifier icon, boolean monochromeIcon, ModInfo info) {
            super(info);
            setMargins(HeaderEntry.HEADER_MARGINS);
            this.title = title;
            this.icon = icon;
            this.version = version;
            this.monochromeIcon = monochromeIcon;
            this.iconColour = info.theme().themeLighter;
        }

        @Override
        protected int height() {
            return this.font.lineHeight;
        }

        @Override
        public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
            super.renderContent(graphics, mouseX, mouseY, hovered, partialTick);

            final int iconSize = this.icon == null ? 0 : (int) (this.font.lineHeight * 1.5);
            final int gap = 3;

            if(this.icon != null) {
                graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    this.icon,
                    this.getContentX(),
                    this.getContentY() - (iconSize / 4),
                    0,
                    0,
                    iconSize,
                    iconSize,
                    iconSize,
                    iconSize,
                    iconSize,
                    iconSize,
                    this.monochromeIcon ? this.iconColour : -1
                );
            }

            graphics.drawString(this.font, this.title, this.getContentX() + iconSize + gap, this.getContentY(), -1);

            int versionWidth = this.font.width(this.version);
            graphics.drawString(this.font, this.version, this.getContentRight() - versionWidth, this.getContentY(), -1);
        }

        @Override
        protected int accentTop() {
            return super.accentTop() + Entry.ACCENT_TOPMOST_OFFSET;
        }

        @Override
        public List<? extends AbstractWidget> widgetChildren() {
            return List.of();
        }

        @Override
        public List<? extends NarratableEntry> narratableChildren() {
            return List.of();
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }
    }

    public record WidgetPosition(int entryIndex, boolean secondary) {
    }

    public record ModInfo(String id, ColorTheme theme) {
    }
}
