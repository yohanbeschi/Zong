package com.xenoage.zong.musicxml.types.enums;

import com.xenoage.utils.xml.XmlReader;
import com.xenoage.utils.xml.XmlWriter;

/**
 * MusicXML valign.
 * 
 * @author Andreas Wenger
 */
public enum MxlVAlign {

	Top,
	Middle,
	Bottom,
	Baseline,
	Unknown;

	public static final String attrName = "valign";


	public static MxlVAlign read(XmlReader reader) {
		return Utils.readOr(attrName, reader.getAttribute(attrName), values(), Unknown);
	}

	public void write(XmlWriter writer) {
		if (this != Unknown)
			writer.writeAttribute(attrName, toString().toLowerCase());
	}

}
