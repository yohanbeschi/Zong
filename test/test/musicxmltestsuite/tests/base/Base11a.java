package musicxmltestsuite.tests.base;

import com.xenoage.zong.core.music.time.TimeType;


public interface Base11a
	extends Base {

	@Override default String getFileName() {
		return "11a-TimeSignatures.xml";
	}

	TimeType[] expectedTimes = {
		TimeType.timeAllaBreve, TimeType.timeCommon, TimeType.time_2_2,
		TimeType.timeType(3, 2), TimeType.time_2_4, TimeType.time_3_4, TimeType.time_4_4,
		TimeType.timeType(5, 4), TimeType.timeType(3, 8), TimeType.time_6_8, TimeType.timeType(12, 8) };
}
