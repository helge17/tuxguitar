package org.herac.tuxguitar.midiinput;

import java.util.Iterator;
import java.util.TreeSet;
//import java.util.Arrays;	// just for debugging

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.tools.TGSelectScaleAction;
import org.herac.tuxguitar.app.tools.scale.ScaleManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;

class MiScaleFinder
{
	static class TestScale
	{
		int		f_Key;
		int		f_ScaleSize;
		int[]	f_Sequence;
	}


	static private int[]	scaleDefToModel(String inScaleDefinition)
	{
	String[]	keys	= inScaleDefinition.split(",");
	int[]		model	= new int[keys.length];

	for(int i = 0; i < keys.length; i++)
		model[i] = (Integer.parseInt(keys[i]) - 1);

	return(model);
	}


	static private int[]	scaleModelToIntervals(int[] inModel)
	{
	int[]		intervals = new int[inModel.length];

	for(int i = 1; i < inModel.length; i++)
		intervals[i - 1] = inModel[i] - inModel[i - 1];

	intervals[inModel.length - 1] = 12 - inModel[inModel.length - 1];

	return(intervals);
	}


	static private TestScale[]	buildReferenceSequences(int inLoPitch, int inHiPitch, int inScaleIndex)
	{
	ScaleManager	scaleMgr	= TuxGuitar.getInstance().getScaleManager();
	int[]			model		= scaleDefToModel(scaleMgr.getScaleKeys(inScaleIndex));
	int[]			intervals	= scaleModelToIntervals(model);
	TestScale[]		sequences	= new TestScale[12];

	//System.out.println();
	//System.out.println("Scale: "			+ scaleMgr.getScaleName(inScaleIndex));
	//System.out.println("Lowest Pitch: "	+ inLoPitch);
	//System.out.println("Highest Pitch: "	+ inHiPitch);
	//System.out.println("Model: "			+ Arrays.toString(model));
	//System.out.println("Intervals: "		+ Arrays.toString(intervals));

	// build sequences, backwards, one per key
	for(int key = 0; key < 12; key++)
		{
		// compute sequence length
		int			sequenceLength = 0;

		for(int pitch = inLoPitch - key, intervalsIndex = 0; pitch <= inHiPitch;)
			{
			sequenceLength++;
			pitch += intervals[intervalsIndex];
			intervalsIndex = (intervalsIndex + 1 >= intervals.length ? 0 : intervalsIndex + 1);
			}

		// initialize sequence
		sequences[key]				= new TestScale();
		sequences[key].f_Key		= (inLoPitch - key) % 12;
		sequences[key].f_ScaleSize	= model.length;
		sequences[key].f_Sequence	= new int[sequenceLength];

		// fill sequence
		for(int pitch = inLoPitch - key, intervalsIndex = 0, i = 0; pitch <= inHiPitch;)
			{
			sequences[key].f_Sequence[i++] = pitch;
			pitch += intervals[intervalsIndex];
			intervalsIndex = (intervalsIndex + 1 >= intervals.length ? 0 : intervalsIndex + 1);
			}

		//System.out.println("key: " + key + ", sequence: " + Arrays.toString(sequences[key].f_Sequence));
		}

	return(sequences);
	}


	static private int		countMatches(TreeSet<Byte> inScale, int[] inRefSequence)
	{
	int			count	= 0;
	Iterator<Byte> it		= inScale.iterator();

	while(it.hasNext())
		{
		int		pitch = ((Byte)it.next()).intValue();
		boolean	found = false;

		for(int i = 0; i < inRefSequence.length && !found; i++)
			if(pitch == inRefSequence[i])
				found = true;

		if(!found)
			return(0);
		else
			count++;
		}

	return(count);
	}


	static public int		findMatchingScale(TreeSet<Byte> inScale)
	{
	ScaleManager	scaleMgr		= TuxGuitar.getInstance().getScaleManager();
	int				scalesCount		= scaleMgr.countScales(),
					minScaleSize	= 12,
					maxMatches		= 0,
					scaleIndex		= ScaleManager.NONE_SELECTION,
					scaleKey		= 0;

	if(!inScale.isEmpty())
		{
		int		loPitch	= ((Byte)inScale.first()).intValue(),
				hiPitch	= ((Byte)inScale.last()).intValue();

		//System.out.println("Input: "	+ inScale);
		//System.out.println("loPitch: "	+ loPitch);
		//System.out.println("hiPitch: "	+ hiPitch);

		for(int s = 0; s < scalesCount; s++)
			{
			TestScale[]	refSequences = buildReferenceSequences(loPitch, hiPitch, s);

			for(int key = 0; key < 12; key++)
				{
				int		matches = countMatches(inScale, refSequences[key].f_Sequence);

				if(	matches > maxMatches)
					{
					maxMatches		= matches;
					scaleIndex		= s;
					scaleKey		= refSequences[key].f_Key;
					minScaleSize	= refSequences[key].f_ScaleSize;

					//System.out.println();
					//System.out.println("more matches: " + scaleMgr.getScaleName(scaleIndex));
					//System.out.println("maxMatches: " + maxMatches + " minScaleSize: " + minScaleSize);
					}
				else if(maxMatches > 0 && matches == maxMatches && refSequences[key].f_ScaleSize < minScaleSize)
					{
					maxMatches		= matches;
					scaleIndex		= s;
					scaleKey		= refSequences[key].f_Key;
					minScaleSize	= refSequences[key].f_ScaleSize;

					//System.out.println();
					//System.out.println("smaller scale: " + scaleMgr.getScaleName(scaleIndex));
					//System.out.println("maxMatches: " + maxMatches + " minScaleSize: " + minScaleSize);
					}
				}
			}
		}

	selectScale(scaleIndex, scaleKey);
	return(scaleIndex);
	}


	static public void		selectScale(final int inIndex, final int inKey)
	{
		TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(), TGSelectScaleAction.NAME);
		tgActionProcessor.setAttribute(TGSelectScaleAction.ATTRIBUTE_INDEX, inIndex);
		tgActionProcessor.setAttribute(TGSelectScaleAction.ATTRIBUTE_KEY, inKey);
		tgActionProcessor.process();
//	try	{
//		TGSynchronizer.instance().execute( new Runnable() {
//			public void run() throws TGException {
//				TuxGuitar.getInstance().getScaleManager().selectScale(inIndex, inKey);
//			}
//		});
//		}
//	catch(Throwable e) {
//		e.printStackTrace();
//		}
	}
}