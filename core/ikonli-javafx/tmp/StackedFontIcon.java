package org.kordamp.ikonli.javafx;

import com.sun.javafx.css.converters.PaintConverter;
import com.sun.javafx.css.converters.SizeConverter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.*;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.Ikon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class StackedFontIcon extends StackPane implements Icon {
    private StyleableIntegerProperty iconSize;
    private StyleableObjectProperty<Paint> iconColor;
    private ObjectProperty<Ikon[]> iconCodes;
    private ObjectProperty<Number[]> iconSizes;
    private ChangeListener<Paint> iconColorChangeListener = (v, o, n) -> setIconColorOnChildren(n);
    private ChangeListener<Ikon[]> iconCodesChangeListener = (v, o, n) -> updateIconCodes(n);
    private ChangeListener<Number[]> iconSizesChangeListener = (v, o, n) -> updateIconSizes(n);
    private ChangeListener<Number> iconSizeChangeListener = (v, o, n) -> setIconSizeOnChildren(n.intValue());

    public StackedFontIcon() {
        getStyleClass().setAll("stacked-ikonli-font-icon");
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    public ObjectProperty<Ikon[]> iconCodesProperty() {
        if (iconCodes == null) {
            iconCodes = new StyleableObjectProperty<Ikon[]>() {
                @Override
                public Object getBean() {
                    return StackedFontIcon.this;
                }

                @Override
                public String getName() {
                    return "iconCodes";
                }

                @Override
                public CssMetaData<? extends Styleable, Ikon[]> getCssMetaData() {
                    return StyleableProperties.ICON_CODES;
                }
            };
            iconCodes.addListener(iconCodesChangeListener);
        }
        return iconCodes;
    }

    public IntegerProperty iconSizeProperty() {
        if (iconSize == null) {
            iconSize = new StyleableIntegerProperty(16) {
                @Override
                public CssMetaData getCssMetaData() {
                    return StyleableProperties.ICON_SIZE;
                }

                @Override
                public Object getBean() {
                    return StackedFontIcon.this;
                }

                @Override
                public String getName() {
                    return "iconSize";
                }
            };
            iconSize.addListener(iconSizeChangeListener);
        }
        return iconSize;
    }

    public ObjectProperty<Number[]> iconSizesProperty() {
        if (iconSizes == null) {
            iconSizes = new StyleableObjectProperty<Number[]>() {
                @Override
                public Object getBean() {
                    return StackedFontIcon.this;
                }

                @Override
                public String getName() {
                    return "iconSizes";
                }

                @Override
                public CssMetaData<? extends Styleable, Number[]> getCssMetaData() {
                    return StyleableProperties.ICON_SIZES;
                }
            };
            iconSizes.addListener(iconSizesChangeListener);
        }
        return iconSizes;
    }

    public ObjectProperty<Paint> iconColorProperty() {
        if (iconColor == null) {
            iconColor = new StyleableObjectProperty<Paint>(Color.BLACK) {
                @Override
                public CssMetaData getCssMetaData() {
                    return StyleableProperties.ICON_COLOR;
                }

                @Override
                public Object getBean() {
                    return StackedFontIcon.this;
                }

                @Override
                public String getName() {
                    return "iconColor";
                }
            };
            iconColor.addListener(iconColorChangeListener);
        }
        return iconColor;
    }

    public int getIconSize() {
        return iconSizeProperty().get();
    }

    public void setIconSize(int size) {
        if (size <= 0) {
            throw new IllegalStateException("Argument 'size' must be greater than zero.");
        }
        iconSizeProperty().set(size);
    }

    public Paint getIconColor() {
        return iconColorProperty().get();
    }

    public void setIconColor(Paint paint) {
        requireNonNull(paint, "Argument 'paint' must not be null");
        iconColorProperty().set(paint);
    }

    public Ikon[] getIconCodes() {
        Ikon[] iconFonts = iconCodesProperty().get();
        return Arrays.copyOf(iconFonts, iconFonts.length);
    }

    public void setIconCodes(Ikon[] iconCodes) {
        getChildren().clear();
        initializeSizesIfNeeded(iconCodes);
        iconCodesProperty().set(iconCodes);
    }

    public void setIkons(Ikon... iconCodes) {
        getChildren().clear();
        initializeSizesIfNeeded(iconCodes);
        iconCodesProperty().set(iconCodes);
    }

    public void setIconCodeLiterals(String... iconCodes) {
        getChildren().clear();
        Ikon[] codes = new Ikon[iconCodes.length];
        for (int i = 0; i < iconCodes.length; i++) {
            codes[i] = IkonResolver.getInstance().resolveIkonHandler(iconCodes[i]).resolve(iconCodes[i]);
        }
        initializeSizesIfNeeded(iconCodes);
        iconCodesProperty().set(codes);
    }

    private void initializeSizesIfNeeded(Object[] iconCodes) {
        if (iconSizesProperty().get() == null) {
            Double[] sizes = new Double[iconCodes.length];
            Arrays.fill(sizes, 1d);
            iconSizesProperty().set(sizes);
        }
    }

    private void updateIconCodes(Ikon[] iconCodes) {
        for (int index = 0; index < iconCodes.length; index++) {
            getChildren().add(createFontIcon(iconCodes[index], index));
        }
    }

    private FontIcon createFontIcon(Ikon iconCode, int index) {
        FontIcon icon = new FontIcon(iconCode);
        icon.setIconSize(getIconSize());
        icon.setIconColor(getIconColor());
        int size = icon.getIconSize();
        applySizeToIcon(size, icon, index);
        return icon;
    }

    public Number[] getIconSizes() {
        Number[] iconSizes = iconSizesProperty().get();
        return Arrays.copyOf(iconSizes, iconSizes.length);
    }

    public void setIconSizes(Number[] iconSizes) {
        iconSizesProperty().set(iconSizes);
    }

    public void setSizes(double... iconSizes) {
        Number[] array = new Number[iconSizes.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = iconSizes[i];
        }
        iconSizesProperty().set(array);
    }

    private void updateIconSizes(Number[] iconSizes) {
        iconSizesProperty().set(iconSizes);
        setIconSizeOnChildren(getIconSize());
    }

    public void setColors(Paint... iconColors) {
        int i = 0;
        for (Node node : getChildren()) {
            if (node instanceof Icon) {
                ((Icon) node).setIconColor(iconColors[i++]);
            }
        }
    }

    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return StackedFontIcon.getClassCssMetaData();
    }

    private void setIconSizeOnChildren(int size) {
        int i = 0;
        for (Node node : getChildren()) {
            if (node instanceof Icon) {
                applySizeToIcon(size, (Icon) node, i++);
            }
        }
    }

    private void applySizeToIcon(int size, Icon icon, int index) {
        double childPercentageSize = iconSizesProperty().get()[index].doubleValue();
        double newSize = size * childPercentageSize;
        icon.setIconSize((int) newSize);
    }

    private void setIconColorOnChildren(Paint color) {
        for (Node node : getChildren()) {
            if (node instanceof Icon) {
                ((Icon) node).setIconColor(color);
            }
        }
    }

    private static class StyleableProperties {
        private static final CssMetaData<StackedFontIcon, Number> ICON_SIZE =
                new CssMetaData<StackedFontIcon, Number>("-fx-icon-size",
                        SizeConverter.getInstance(), 16.0) {

                    @Override
                    public boolean isSettable(StackedFontIcon fontIcon) {
                        return true;
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(StackedFontIcon icon) {
                        return (StyleableProperty<Number>) icon.iconSizeProperty();
                    }
                };

        private static final CssMetaData<StackedFontIcon, Paint> ICON_COLOR =
                new CssMetaData<StackedFontIcon, Paint>("-fx-icon-color",
                        PaintConverter.getInstance(), Color.BLACK) {

                    @Override
                    public boolean isSettable(StackedFontIcon node) {
                        return true;
                    }

                    @Override
                    public StyleableProperty<Paint> getStyleableProperty(StackedFontIcon icon) {
                        return (StyleableProperty<Paint>) icon.iconColorProperty();
                    }
                };

        private static final CssMetaData<StackedFontIcon, Ikon[]> ICON_CODES =
                new CssMetaData<StackedFontIcon, Ikon[]>("-fx-icon-codes",
                        FontIconConverter.SequenceConverter.getInstance(), null) {

                    @Override
                    public boolean isSettable(StackedFontIcon node) {
                        return true;
                    }

                    @Override
                    public StyleableProperty<Ikon[]> getStyleableProperty(StackedFontIcon icon) {
                        return (StyleableProperty<Ikon[]>) icon.iconCodesProperty();
                    }
                };

        private static final CssMetaData<StackedFontIcon, Number[]> ICON_SIZES =
                new CssMetaData<StackedFontIcon, Number[]>("-fx-icon-sizes",
                        SizeConverter.SequenceConverter.getInstance(), null) {

                    @Override
                    public boolean isSettable(StackedFontIcon node) {
                        return true;
                    }

                    @Override
                    public StyleableProperty<Number[]> getStyleableProperty(StackedFontIcon icon) {
                        return (StyleableProperty<Number[]>) icon.iconSizesProperty();
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<CssMetaData<? extends Styleable, ?>>(StackPane.getClassCssMetaData());
            styleables.add(ICON_SIZE);
            styleables.add(ICON_COLOR);
            styleables.add(ICON_CODES);
            styleables.add(ICON_SIZES);
            STYLEABLES = unmodifiableList(styleables);
        }
    }
}
