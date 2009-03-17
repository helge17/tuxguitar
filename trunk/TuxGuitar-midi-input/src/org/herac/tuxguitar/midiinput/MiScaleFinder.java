package org.herac.tuxguitar.midiinput;

import java.util.Iterator;
import java.util.TreeSet;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.tools.scale.ScaleManager;

class MiScaleFinder
{
	static private int[]	scaleDefToArray(String inScaleDefinition)
	{
	String[]	keys		= inScaleDefinition.split(",");
	int[]		sequence	= new int[keys.length];

	for(int i = 0; i < keys.length; i++)
		sequence[i] = (Integer.parseInt(keys[i]) - 1);

	return(sequence);
	}


	static private int[]	buildReferenceSequence(TreeSet inScale, int inScaleIndex)
	{
	ScaleManager	scaleMgr	= TuxGuitar.instance().getScaleManager();
	int				loPitch		= ((Byte)inScale.first()).intValue(),
					hiPitch		= ((Byte)inScale.last()).intValue(),
					scaleSpan	= hiPitch - loPitch;
	int[]			model		= scaleDefToArray(scaleMgr.getScaleKeys(inScaleIndex));
	int				modelSpan	= model[model.length - 1],
					repetitions	= scaleSpan / modelSpan + (scaleSpan % modelSpan > 0 ? 1 : 0);
	int[]			sequence	= new int[model.length * repetitions];

	for(int r = 0; r < repetitions; r++)
		for(int i = 0; i < model.length; i++)
			sequence[model.length * r + i] = loPitch + r * 12 + model[i];

	return(sequence);
	}


	static private int		countMatches(TreeSet inScale, int[] inRefSequence)
	{
	int			count	= 0;
	Iterator	it		= inScale.iterator();

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


	static public int		findMatchingScale(TreeSet inScale)
	{
	ScaleManager	scaleMgr		= TuxGuitar.instance().getScaleManager();
	int				scalesCount		= scaleMgr.countScales(),
					minScaleSize	= 12,
					maxMatches		= 0,
					scaleIndex		= ScaleManager.NONE_SELECTION;

	if(!inScale.isEmpty())
		{
		for(int m = 0; m < scalesCount; m++)
			{
			int[]	refSequence	= buildReferenceSequence(inScale, m);
			int		matches		= countMatches(inScale, refSequence);
	
			// choose the scale with the maximum number of matches
			// in case of a draw, choose the smallest scale
			if(	matches > maxMatches ||
				(maxMatches > 0 && matches == maxMatches && refSequence.length < minScaleSize))
				{
				maxMatches		= matches;
				scaleIndex		= m;
				minScaleSize	= refSequence.length;
				}
			}
		}

	return(scaleIndex);
	}
}