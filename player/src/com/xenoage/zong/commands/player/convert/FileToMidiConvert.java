package com.xenoage.zong.commands.player.convert;

import static com.xenoage.utils.error.Err.handle;
import static com.xenoage.utils.log.Log.log;
import static com.xenoage.utils.log.Report.remark;
import static com.xenoage.utils.log.Report.warning;
import static com.xenoage.zong.desktop.App.app;
import static com.xenoage.zong.desktop.gui.utils.FileChooserUtils.addFilter;
import static com.xenoage.zong.desktop.gui.utils.FileChooserUtils.rememberDir;
import static com.xenoage.zong.desktop.gui.utils.FileChooserUtils.setDirFromSettings;
import static com.xenoage.zong.player.Player.pApp;

import java.io.File;
import java.util.List;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

import lombok.AllArgsConstructor;

import com.xenoage.utils.document.command.TransparentCommand;
import com.xenoage.utils.document.io.FileFormat;
import com.xenoage.utils.document.io.SupportedFormats;
import com.xenoage.utils.filter.AllFilter;
import com.xenoage.utils.iterators.It;
import com.xenoage.zong.Voc;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.desktop.io.midi.out.JseMidiSequenceWriter;
import com.xenoage.zong.io.midi.out.MidiConverter;

/**
 * This command lets the user select a MusicXML file which is then
 * converted into a MIDI file.
 * 
 * MusicXML files with multiple scores are also supported. In this case
 * each score gets its own MIDI file.
 * 
 * @author Andreas Wenger
 */
@AllArgsConstructor public class FileToMidiConvert
	extends TransparentCommand {

	private Window ownerWindow;


	@Override public void execute() {
		FileChooser fileChooser = new FileChooser();
		//use last document directory
		setDirFromSettings(fileChooser);
		//add filters
		SupportedFormats<?> supportedFormats = app().getSupportedFormats();
		for (FileFormat<?> fileFormat : supportedFormats.getReadFormats()) {
			addFilter(fileChooser, fileFormat);
		}
		//show the dialog
		File file = fileChooser.showOpenDialog(ownerWindow);
		if (file != null) {
			log(remark("Dialog closed (OK), converting file \"" + file.getName() + "\""));
			//save document directory
			rememberDir(file);
			//convert - TODO: show progress
			String lastPath = file.getAbsolutePath();
			List<Score> scores = pApp().loadMxlScores(lastPath, new AllFilter<String>());
			boolean useNumber = scores.size() > 1;
			It<Score> scoresIt = new It<Score>(scores);

			for (Score score : scoresIt) {
				Sequence seq = MidiConverter.convertToSequence(
					score, false, false, new JseMidiSequenceWriter()).getSequence();
				String newPath = lastPath;
				String number = (useNumber ? ("-" + (scoresIt.getIndex() + 1)) : "");

				if (newPath.toLowerCase().endsWith(".xml") || //TIDY: share code: FilenameUtils.numberFiles
					newPath.toLowerCase().endsWith(".mxl")) {
					newPath = newPath.substring(0, newPath.length() - 4);
				}

				newPath += (number + ".mid");

				try {
					MidiSystem.write(seq, 1, new File(newPath));
				} catch (Exception ex) {
					handle(warning(Voc.ErrorSavingFile));
				}
			}
		}
		else {
			log(remark("Dialog closed (Cancel)"));
		}
	}

}
