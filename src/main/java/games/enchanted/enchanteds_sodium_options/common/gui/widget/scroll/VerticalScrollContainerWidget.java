package games.enchanted.enchanteds_sodium_options.common.gui.widget.scroll;

import games.enchanted.enchanteds_sodium_options.common.Logging;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import games.enchanted.enchanteds_sodium_options.common.util.InputUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class VerticalScrollContainerWidget<C extends VerticalScrollContainerWidget.Child> extends AbstractContainerWidget {
    private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("widget/scroller");
    private static final Identifier SCROLLER_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("widget/scroller_background");
    private static final int SCROLLBAR_MIN_HEIGHT = 32;
    private static final int DEFAULT_SCROLLBAR_WIDTH = 6;
    private static final int DEFAULT_SCROLL_RATE = 6;

    private final List<C> children = new ArrayList<>();
    @Nullable private C hoveredChild = null;
    @Nullable private C focusedChild = null;

    public VerticalScrollContainerWidget(int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY, new ScrollbarSettings(
            SCROLLER_SPRITE,
            null,
            SCROLLER_BACKGROUND_SPRITE,
            DEFAULT_SCROLLBAR_WIDTH,
            SCROLLBAR_MIN_HEIGHT,
            DEFAULT_SCROLL_RATE,
            false
        ));
    }

    @Override
    protected int contentHeight() {
        int totalHeight = 0;
        for (C child : children) {
            totalHeight += child.getHeight();
        }
        return totalHeight;
    }

    @Override
    public List<? extends C> children() {
        return Collections.unmodifiableList(children);
    }

    public int getNextChildY() {
        int top = this.getY() - (int)this.scrollAmount();
        for (C child : this.children) {
            top += child.getHeight();
        }
        return top;
    }

    public void addChild(C child) {
        child.setX(this.getRowLeft());
        child.setWidth(this.getRowWidth());
        child.setY(this.getNextChildY());
        child.setHeight(height);
        this.children.add(child);
        this.repositionElements();
    }

    public abstract void visitChildren(Consumer<AbstractWidget> visitor);

    public void repositionElements() {
        int top = this.getY() - (int)this.scrollAmount();

        for (C child : this.children) {
            child.setY(top);
            top += child.getHeight();
            child.setX(this.getRowLeft());
            child.setWidth(this.getRowWidth());
        }
    }

    protected final @Nullable C getChildAtPosition(double x, double y) {
        for (C child : this.children) {
            if (child.isMouseOver(x, y)) return child;
        }
        return null;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        this.hoveredChild = this.isMouseOver(mouseX, mouseY) ? this.getChildAtPosition(mouseX, mouseY) : null;
        this.renderBackground(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
        this.enableScissor(GuiGraphicsExtractor);
        this.renderChildren(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
        GuiGraphicsExtractor.disableScissor();
        this.extractScrollbar(GuiGraphicsExtractor, mouseX, mouseY);
        if(InputUtil.shouldShowDebugWidgetBound()) {
            GuiGraphicsExtractor.outline(this.getX(), this.getY(), this.width, this.height, 0xff9f7252);
        }
    }

    protected void renderBackground(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
    }

    protected void enableScissor(final GuiGraphicsExtractor graphics) {
        graphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
    }

    protected void renderChildren(final GuiGraphicsExtractor GuiGraphicsExtractor, final int mouseX, final int mouseY, final float partialTicks) {
        for (C child : this.children) {
            if (child.getY() + child.getHeight() >= this.getY() && child.getY() <= this.getBottom()) {
                if(InputUtil.shouldShowDebugWidgetBound()) {
                    GuiGraphicsExtractor.outline(child.getX(), child.getY(), child.getWidth(), child.getHeight(), 0xffc57cb9);
                    GuiGraphicsExtractor.outline(child.getContentX(), child.getContentY(), child.getContentWidth(), child.getContentHeight(), 0xff56a8f5);
                }
                child.extractContent(GuiGraphicsExtractor, mouseX, mouseY, this.hoveredChild == child, partialTicks);
            }
        }
    }

    @Override
    public void setScrollAmount(double scrollAmount) {
        super.setScrollAmount(scrollAmount);
        this.repositionElements();
    }

    @Override
    protected double scrollRate() {
        return 35;
    }

    @Override
    protected int scrollBarX() {
        return this.getRowRight() + Math.abs(this.getRowWidth() - this.getWidth()) / 2 - 1;
    }

    private void scrollBy(int amount) {
        this.setScrollAmount(this.scrollAmount() + amount);
    }

    protected void scrollToChild(C child) {
        int distFromTop = child.getY() - this.getY();
        if (distFromTop < 0) {
            this.scrollBy(distFromTop);
        }

        int distFromBottom = this.getBottom() - child.getY() - child.getHeight();
        if (distFromBottom < 0) {
            this.scrollBy(-distFromBottom);
        }
    }

    @Override
    public Optional<GuiEventListener> getChildAt(double mouseX, double mouseY) {
        return Optional.<GuiEventListener>ofNullable(this.getChildAtPosition(mouseX, mouseY));
    }

    @Override
    public void setFocused(boolean focus) {
        super.setFocused(focus);
        if (!focus) {
            this.setFocused(null);
        }
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focus) {
        GuiEventListener prevFocus = this.getFocused();
        if (prevFocus != focus && prevFocus instanceof ContainerEventHandler oldFocusContainer) {
            oldFocusContainer.setFocused(null);
        }

        int newFocusIndex = this.children.indexOf(focus);
        if (newFocusIndex >= 0) {
            C newFocusChild = this.children.get(newFocusIndex);
            this.setSelected(newFocusChild);
        }

        super.setFocused(focus);
    }

    public void setSelected(@Nullable C select) {
        this.focusedChild = select;
        if (select != null) {
            boolean topClipped = select.getContentY() < this.getY();
            boolean bottomClipped = select.getPaddedBottom() > this.getBottom();
            if (Minecraft.getInstance().getLastInputType().isKeyboard() || topClipped || bottomClipped) {
                this.scrollToChild(select);
            }
        }
    }

    @Nullable
    protected C nextChild(ScreenDirection screenDirection, Predicate<C> selectionPredicate, @Nullable C entry) {
        if(screenDirection.getAxis() == ScreenAxis.HORIZONTAL) return null;
        if (this.children().isEmpty()) return null;

        int indexDirection = screenDirection == ScreenDirection.UP ? -1 : 1;
        int index;
        if (entry == null) {
            index = indexDirection > 0 ? 0 : this.children().size() - 1;
        } else {
            index = this.children().indexOf(entry) + indexDirection;
        }

        for (int i = index; i >= 0 && i < this.children.size(); i += indexDirection) {
            C selected = this.children.get(i);
            if (selectionPredicate.test(selected)) {
                return selected;
            }
        }

        return null;
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(final FocusNavigationEvent navigationEvent) {
        if(!this.visible) {
            return null;
        }
        if (this.children().isEmpty()) {
            return null;
        }
        if (!(navigationEvent instanceof FocusNavigationEvent.ArrowNavigation arrowNavigation)) {
            return super.nextFocusPath(navigationEvent);
        }

        @SuppressWarnings("unchecked")
        C focused = (C) this.getFocused();
        ScreenDirection navigationDirection = arrowNavigation.direction();

        if (navigationDirection.getAxis() == ScreenAxis.HORIZONTAL && focused != null) {
            return ComponentPath.path(this, focused.nextFocusPath(navigationEvent));
        }

        int index = -1;
        if (focused != null) {
            index = focused.children().indexOf(focused.getFocused());
        }

        if (index == -1) {
            switch (navigationDirection) {
                case LEFT:
                    index = Integer.MAX_VALUE;
                    navigationDirection = ScreenDirection.DOWN;
                    break;
                case RIGHT:
                    index = 0;
                    navigationDirection = ScreenDirection.DOWN;
                    break;
                default:
                    index = 0;
            }
        }

        ComponentPath componentPath = null;
        while (componentPath == null) {
            focused = this.nextChild(navigationDirection, child -> !child.children().isEmpty(), focused);
            if (focused == null) {
                return null;
            }

            componentPath = focused.focusPathAtIndex(arrowNavigation, index);
        }

        return ComponentPath.path(this, componentPath);
    }

    public int getRowLeft() {
        return this.getX() + (this.scrollable() ? 0 : SCROLLBAR_WIDTH);
    }

    public int getRowRight() {
        return this.getRowLeft() + this.getRowWidth();
    }

    public int getRowTop(final int row) {
        return this.children.get(row).getY();
    }

    public int getRowBottom(final int row) {
        C child = this.children.get(row);
        return child.getY() + child.getHeight();
    }

    public int getRowWidth() {
        return this.width - scrollbarWidth();
    }


    @Override
    public NarrationPriority narrationPriority() {
        if (this.isFocused()) {
            return NarrationPriority.FOCUSED;
        } else {
            return this.hoveredChild != null ? NarrationPriority.HOVERED : NarrationPriority.NONE;
        }
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput output) {
        if(this.hoveredChild != null) {
            this.hoveredChild.updateNarration(output);
            this.narrateChildPosition(output, this.hoveredChild);
        }
        if(this.focusedChild != null) {
            this.focusedChild.updateNarration(output);
            this.narrateChildPosition(output, this.focusedChild);
        }
    }

    protected void narrateChildPosition(NarrationElementOutput output, C child) {
        List<C> children = this.children;
        if (children.size() > 1) {
            int childIndex = children.indexOf(child);
            if (childIndex == -1) return;
            output.add(NarratedElementType.POSITION, Component.translatable("narrator.position.list", childIndex + 1, children.size()));
        }
    }

    public static abstract class Child implements ContainerEventHandler {
        @Nullable NarratableEntry lastNarratable = null;
        @Nullable GuiEventListener focusedElement = null;

        private Margin margins = new Margin(2, 2);

        int height = 0;
        int width = 0;
        int x = 0;
        int y = 0;

        private boolean dragging;

        protected int height() {
            return this.height;
        }

        final public int getHeight() {
            return height() + getMargins().totalBlock();
        }

        public void setHeight(int height) {
            this.height = height;
        }

        protected int width() {
            return this.width;
        }

        public final int getWidth() {
            return width();
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getX() {
            return this.x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return this.y;
        }

        public void setY(int y) {
            this.y = y;
        }


        public int getContentX() {
            return this.getX() + getMargins().left();
        }

        public int getContentY() {
            return this.getY() + getMargins().top();
        }

        public int getContentHeight() {
            return this.getHeight() - getMargins().totalBlock();
        }

        public int getContentYMiddle() {
            return this.getContentY() + (this.getContentHeight() / 2);
        }

        public int getContentXMiddle() {
            return this.getContentX() + (this.getContentWidth() / 2);
        }

        public int getContentBottom() {
            return this.getContentY() + this.getContentHeight();
        }

        public int getContentWidth() {
            return this.getWidth() - this.getMargins().totalInline();
        }

        public int getContentRight() {
            return this.getContentX() + this.getContentWidth();
        }

        public int getPaddedBottom() {
            return this.getContentBottom() + getMargins().bottom();
        }

        public int getPaddedRight() {
            return this.getContentRight() + getMargins().right();
        }


        public Margin getMargins() {
            return this.margins;
        }

        public void setMargins(Margin margins) {
            this.margins = margins;
        }

        @Override
        public ScreenRectangle getRectangle() {
            return new ScreenRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.getRectangle().containsPoint((int) mouseX, (int) mouseY);
        }

        @Override
        public boolean isDragging() {
            return this.dragging;
        }

        @Override
        public void setDragging(final boolean dragging) {
            this.dragging = dragging;
        }

        @Override
        public void setFocused(@Nullable GuiEventListener newFocus) {
            if (this.focusedElement != null) {
                this.focusedElement.setFocused(false);
            }
            if (newFocus != null) {
                newFocus.setFocused(true);
            }
            if (!this.isDragging()) {
                this.focusedElement = newFocus;
            }
        }

        @Override
        public @Nullable GuiEventListener getFocused() {
            return this.focusedElement;
        }

        public @Nullable ComponentPath focusPathAtIndex(FocusNavigationEvent navigationEvent, int currentIndex) {
            if (this.children().isEmpty()) return null;
            ComponentPath componentPath = this.children().get(Math.min(currentIndex, this.children().size() - 1)).nextFocusPath(navigationEvent);
            return ComponentPath.path(this, componentPath);
        }

        @Nullable
        @Override
        public ComponentPath nextFocusPath(FocusNavigationEvent navigationEvent) {
            if (!(navigationEvent instanceof FocusNavigationEvent.ArrowNavigation(ScreenDirection direction, @Nullable ScreenRectangle previousFocus))) {
                return ContainerEventHandler.super.nextFocusPath(navigationEvent);
            }
            if (direction == ScreenDirection.UP || direction == ScreenDirection.DOWN) return null;

            int indexDirection = direction == ScreenDirection.LEFT ? -1 : 1;
            int indexOfFocus;
            try {
                 indexOfFocus = this.children().indexOf(this.getFocused());
            } catch (Exception e) {
                return ContainerEventHandler.super.nextFocusPath(navigationEvent);
            }

            int index = Mth.clamp(indexDirection + indexOfFocus, 0, this.children().size() - 1);

            for (int i = index; i >= 0 && i < this.children().size(); i += indexDirection) {
                ComponentPath componentPath = this.children().get(i).nextFocusPath(navigationEvent);
                if (componentPath == null) continue;
                return ComponentPath.path(this, componentPath);
            }

            return ContainerEventHandler.super.nextFocusPath(navigationEvent);
        }

        public abstract void extractContent(final GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick);

        public abstract List<? extends AbstractWidget> widgetChildren();

        public abstract List<? extends NarratableEntry> narratableChildren();

        void updateNarration(NarrationElementOutput output) {
            List<? extends NarratableEntry> narratableChildren = this.narratableChildren();
            Screen.NarratableSearchResult result = Screen.findNarratableWidget(narratableChildren, this.lastNarratable);
            if (result == null) return;

            if (result.priority().isTerminal()) {
                this.lastNarratable = result.entry();
            }

            if (narratableChildren.size() > 1) {
                output.add(NarratedElementType.POSITION, Component.translatable("narrator.position.object_list", result.index() + 1, narratableChildren.size()));
            }

            result.entry().updateNarration(output.nest());
        }
    }

    public record Margin(int top, int bottom, int left, int right) {
        public Margin(int inline, int block) {
            this(block, block, inline, inline);
        }

        public int totalBlock() {
            return this.top + this.bottom;
        }

        public int totalInline() {
            return this.left + this.right;
        }
    }
}
