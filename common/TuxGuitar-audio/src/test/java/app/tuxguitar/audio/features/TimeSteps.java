package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.composition.TGChangeTempoRangeAction;
import app.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGTimeSignature;
import app.tuxguitar.util.TGBeatRange;
import io.cucumber.java.en.Given;

/**
 * Steps that edit the song's tempo and time signature, as the composition actions do.
 */
public class TimeSteps {

	private final AudioWorld world;

	public TimeSteps(AudioWorld world) {
		this.world = world;
	}

	private TGMeasureHeader header(int number) {
		return world.song().getMeasureHeader(number - 1);
	}

	@Given("I have set the time signature of measure {int} only to {int}\\/{int}")
	public void setTheTimeSignatureOfOneMeasure(int number, int numerator, int denominator) {
		TGTimeSignature signature = world.fixture().songManager.getFactory().newTimeSignature();
		signature.setNumerator(numerator);
		signature.getDenominator().setValue(denominator);
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header(number));
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TIME_SIGNATURE, signature);
		world.fixture().actionContext.setAttribute(TGChangeTimeSignatureAction.ATTRIBUTE_APPLY_TO_END, Boolean.FALSE);
		world.execute(new TGChangeTimeSignatureAction(world.fixture().context));
		assertEquals(numerator + "/" + denominator, header(number).getTimeSignature().getNumerator() + "/" + header(number).getTimeSignature().getDenominator().getValue());
	}

	// "measure 2 only": the first beat of track 1 in that measure is the selection
	@Given("I have set the tempo of measure {int} only to {int} bpm")
	public void setTheTempoOfOneMeasure(int number, int bpm) {
		TGBeatRange range = TGBeatRange.single(world.fixture().beat(world.track(1).getMeasure(number - 1), 0));
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE, range);
		changeTempo(TGChangeTempoRangeAction.APPLY_TO_SELECTION, bpm);
		assertEquals(bpm, header(number).getTempo().getQuarterValue());
	}

	@Given("I have set the tempo from measure {int} to the end to {int} bpm")
	public void setTheTempoToTheEnd(int number, int bpm) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header(number));
		changeTempo(TGChangeTempoRangeAction.APPLY_TO_END, bpm);
		assertEquals(bpm, header(world.song().countMeasureHeaders()).getTempo().getQuarterValue());
	}

	private void changeTempo(int applyTo, int bpm) {
		world.fixture().actionContext.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_APPLY_TO, applyTo);
		world.fixture().actionContext.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO, bpm);
		world.fixture().actionContext.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO_BASE, TGDuration.QUARTER);
		world.fixture().actionContext.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO_BASE_DOTTED, Boolean.FALSE);
		world.execute(new TGChangeTempoRangeAction(world.fixture().context));
	}
}
