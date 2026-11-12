package app.tuxguitar.midi.synth;

import static app.tuxguitar.midi.synth.FakeProcessors.element;
import static app.tuxguitar.midi.synth.FakeProcessors.program;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

/**
 * Pins the synthesizer program today: a receiver followed by outputs, each enabled or disabled, moved up or down, and
 * how a program is kept in properties.
 */
public class TestProgram {

	private static String read(TGProgram program) {
		List<String> outputs = new ArrayList<String>();
		for (int i = 0; i < program.countOutputs(); i++) {
			outputs.add(program.getOutput(i).getId());
		}
		return program.getReceiver().getId() + " > " + String.join(" > ", outputs);
	}

	@Test
	public void outputsMoveUpAndDownWithinTheChain() {
		TGProgram program = program("source", "a", "b", "c");
		program.moveOutputUp(program.getOutput(2));
		assertEquals("source > a > c > b", read(program));
		program.moveOutputUp(program.getOutput(0));
		assertEquals("source > a > c > b", read(program));
		program.moveOutputDown(program.getOutput(0));
		assertEquals("source > c > a > b", read(program));
		program.moveOutputDown(program.getOutput(2));
		assertEquals("source > c > a > b", read(program));
		program.moveOutputUp(null);
		program.moveOutputDown(null);
		program.moveOutputUp(element("x", "x"));
		program.moveOutputDown(element("x", "x"));
		assertEquals("source > c > a > b", read(program));
	}

	@Test
	public void outputsAreAddedAndRemoved() {
		TGProgram program = program("source", "a", "b");
		program.removeOutput(program.getOutput(0));
		program.removeOutput(null);
		program.removeOutput(element("x", "x"));
		program.addOutput(null);
		assertEquals("source > b", read(program));
		assertNull(program.getOutput(1));
		assertNull(program.getOutput(-1));
	}

	@Test
	public void copyingAProgramCopiesItsElements() {
		TGProgram original = program("source", "a");
		original.getOutput(0).setEnabled(false);
		original.getOutput(0).setParameter("gain", "2");
		TGProgram copy = new TGProgram();
		copy.addOutput(element("old", "old"));
		copy.copyFrom(original);
		assertEquals("source > a", read(copy));
		assertNotSame(original.getOutput(0), copy.getOutput(0));
		assertFalse(copy.getOutput(0).isEnabled());
		assertEquals("a", copy.getOutput(0).getType());
		assertEquals("{gain=2}", copy.getOutput(0).getParameters().toString());
		original.getOutput(0).setParameter("gain", "3");
		assertEquals("{gain=2}", copy.getOutput(0).getParameters().toString());
	}

	@Test
	public void copyingAnEmptyProgramEmptiesIt() {
		TGProgram copy = program("source", "a");
		copy.copyFrom(new TGProgram());
		assertNull(copy.getReceiver());
		assertEquals(0, copy.countOutputs());
	}

	@Test
	public void elementsAreTheSameByTheirId() {
		assertEquals(element("a", "x"), element("a", "y"));
		assertNotEquals(element("a", "x"), element("b", "x"));
		assertEquals("a".hashCode(), element("a", "x").hashCode());
		TGProgramElement noId = new TGProgramElement();
		assertEquals(noId, noId);
		assertNotEquals(noId, new TGProgramElement());
		assertNotEquals(element("a", "x"), "a");
		assertTrue(noId.isEnabled());
	}

	@Test
	public void aProgramIsKeptInPropertiesUnderAPrefix() {
		TGProgram program = program("source", "a", "b");
		program.getReceiver().setParameter("p", "1");
		program.getOutput(1).setEnabled(false);
		TGSynthChannelProperties properties = new TGSynthChannelProperties();
		TGProgramPropertiesUtil.setProgram(properties, "x", program);
		assertEquals("{x.enabled=true, x.id=source, x.output.0.enabled=true, x.output.0.id=a, x.output.0.param.count=0, x.output.0.type=a, "
			+ "x.output.1.enabled=false, x.output.1.id=b, x.output.1.param.count=0, x.output.1.type=b, x.output.count=2, "
			+ "x.param.0.name=p, x.param.0.value=1, x.param.count=1, x.type=source}",
			new TreeMap<String, String>(properties.getProperties()).toString());
		TGProgram read = TGProgramPropertiesUtil.getProgram(properties, "x");
		assertEquals("source > a > b", read(read));
		assertEquals("{p=1}", read.getReceiver().getParameters().toString());
		assertFalse(read.getOutput(1).isEnabled());
	}


	@Test
	public void aProgramWithoutReceiverIsNotKeptButAReceiverWithoutTypeKeepsTheOutputCount() {
		TGSynthChannelProperties properties = new TGSynthChannelProperties();
		TGProgramPropertiesUtil.setProgram(properties, "x", new TGProgram());
		TGProgram noType = new TGProgram();
		noType.setReceiver(element("id", null));
		TGProgramPropertiesUtil.setProgram(properties, "y", noType);
		assertEquals("{y.output.count=0}", properties.getProperties().toString());
		assertNull(TGProgramPropertiesUtil.getProgram(properties, "x"));
		assertNull(TGProgramPropertiesUtil.getProgram(properties, "y"));
	}

	@Test
	public void outputsMissingFromThePropertiesAreLeftOut() {
		TGSynthChannelProperties properties = new TGSynthChannelProperties();
		properties.setValue("x.id", "source");
		properties.setValue("x.type", "source");
		properties.setValue("x.output.count", "2");
		properties.setValue("x.output.1.id", "b");
		properties.setValue("x.output.1.type", "b");
		properties.setValue("x.param.count", "1");
		properties.setValue("x.param.0.value", "orphan");
		TGProgram read = TGProgramPropertiesUtil.getProgram(properties, "x");
		assertEquals("source > b", read(read));
		assertTrue(read.getReceiver().getParameters().isEmpty());
		assertTrue(read.getReceiver().isEnabled());
	}
}
