package com.xenoage.zong.text;

import static com.xenoage.utils.NullUtils.notNull;
import lombok.Data;
import lombok.Getter;

import com.xenoage.utils.annotations.Const;
import com.xenoage.utils.annotations.MaybeNull;
import com.xenoage.utils.annotations.NonNull;
import com.xenoage.utils.color.ColorInfo;
import com.xenoage.utils.font.FontInfo;

/**
 * Style of a {@link FormattedTextString}.
 * 
 * TODO: add derive-methods which return new FormattedTextStyle instances.
 * 
 * Default style: Serif 12pt normal black.
 *
 * @author Andreas Wenger
 */
@Const @Data public class FormattedTextStyle {

	//default values
	public static final Superscript defaultSuperscript = Superscript.Normal;
	public static final ColorInfo defaultColor = ColorInfo.black;
	public static final FormattedTextStyle defaultStyle = new FormattedTextStyle(null, null, null);

	//style properties
	@Getter @NonNull private final FontInfo fontInfo;
	@Getter @NonNull private final ColorInfo color;
	@Getter @NonNull private final Superscript superscript;


	/**
	 * Creates a new {@link FormattedTextStyle} with the given font information.
	 */
	public FormattedTextStyle(@MaybeNull FontInfo fontInfo, @MaybeNull ColorInfo color,
		@MaybeNull Superscript superscript) {
		this.fontInfo = notNull(fontInfo, FontInfo.defaultValue);
		this.color = notNull(color, defaultColor);
		this.superscript = notNull(superscript, defaultSuperscript);
	}

	/**
	 * Creates a new {@link FormattedTextStyle} with the given font information.
	 * For the missing information the default style is used.
	 */
	public FormattedTextStyle(FontInfo fontInfo) {
		this(fontInfo, defaultColor, defaultSuperscript);
	}

	/**
	 * Creates a new {@link FormattedTextStyle} with the given font size.
	 * For the missing information the default style is used.
	 */
	public FormattedTextStyle(float fontSize) {
		this(new FontInfo((String) null, fontSize, null), defaultColor, defaultSuperscript);
	}

	/**
	* Creates a new {@link FormattedTextStyle} with the given font size and color.
	* For the missing information the default style is used.
	*/
	public FormattedTextStyle(float fontSize, ColorInfo color) {
		this(new FontInfo((String) null, fontSize, null), color, defaultSuperscript);
	}

	/**
	 * Creates a new {@link FormattedTextStyle} with the given font information.
	 * For the missing information the default style is used.
	 */
	public FormattedTextStyle() {
		this.fontInfo = null; //not needed, since font is given
		this.superscript = defaultSuperscript;
		this.color = defaultColor;
	}

}