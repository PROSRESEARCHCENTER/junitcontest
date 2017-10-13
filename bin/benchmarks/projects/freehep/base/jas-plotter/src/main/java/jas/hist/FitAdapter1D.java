package jas.hist;

class FitAdapter1D extends Fittable1DFunction
/*
 * adapts a Fittable1DFunction
 * for use by a Fitter class
 */
{
	private double[] fitterParameters, functionParameters;
	// functionParameters are the ones recognized by the
	// function and are updated as the fitterParameters
	// change

	// fitterParameters are the ones recognized by the
	// fitter and may be fewer if not all parameters
	// are being fit
	private boolean[] inFit; // whether the parameter is included
	                         // in the fit
	private Fittable1DFunction func;

	public FitAdapter1D(Fittable1DFunction func)
	{
		this.func = func;
		inFit = func.getIncludeParametersInFit();
		functionParameters = func.getParameterValues();

		double[] temp = new double[functionParameters.length];
		// the possibly shortened list will be stored in temp

		// functionParameters.length is the largest capacity
		// that will be needed

		int j = 0;
		for (int i = 0; i < inFit.length; i++)
			if (inFit[i])
				temp[j++] = functionParameters[i];
		// copy over parameters that are used in the fit

		if (j == functionParameters.length)
			// all parameters were included in the fit
			fitterParameters = temp;
		else
		// the new array is shorter than the old one
		{
			fitterParameters = new double[j];
			// j < functionParameters.length
			// j = number of included parameters
			for (int i = 0; i < j; i++)
				fitterParameters[i] = temp[i];
				// the array fitterParameters is identical
				// to the array temp except that
				// fitterParameters is shorter
		}
	}
	public double[] getParameterValues()
	{
		return fitterParameters;
		// this method is called by the fitter,
		// so it returns the fitterParameters
	}
	public boolean[] getIncludedInFit()
	{
		return inFit;
	}
	public double[] getDerivatives(double x, double[] a) throws FunctionValueUndefined
	{
		updateParameters(a);
		double[] fullAnswer = func.getDerivatives(x, functionParameters);
		// fullAnswer must be reduced if one or more
		// parameters is not being fit
		if (functionParameters.length == fitterParameters.length)
		// all parameters are being fit
			return fullAnswer;

		// else, a condensed answer must be returned
		double[] condensedAnswer = new double[fitterParameters.length];
		int i = 0;

		for (int j = 0; j < fullAnswer.length; j++)
			if (inFit[j])
				condensedAnswer[i++] = fullAnswer[j];
		return condensedAnswer;
	}
	public double valueAt(double x, double[] a) throws FunctionValueUndefined
	{
		updateParameters(a);
		return func.valueAt(x, functionParameters);
	}
	public void setFit(Fitter f, double[] a) throws
		InvalidFunctionParameter
	{
		updateParameters(a);
		func.setFit(f, functionParameters);
	}
	private void updateParameters(double[] fitterParameters)
	// using the fitterParameters, both this.fitterParameters
	// and this.functionParameters are updated
	{
		this.fitterParameters = fitterParameters;

		if (functionParameters.length == fitterParameters.length)
			functionParameters = fitterParameters;
		else
		{
			int i = 0;
			for (int j = 0; j < functionParameters.length; j++)
			{
				if (inFit[j])
				{
					functionParameters[j] = fitterParameters[i++];
				}
			}
		}
	}
	
	// I don't think the remaining methods
	// will ever be called, but they are
	// abstract methods in Basic1DFunction
	// and must be included
	public boolean[] getIncludeParametersInFit()
	{
		return inFit;
	}
	public String[] getParameterNames()
	{
		return func.getParameterNames();
	}
	public String getTitle()
	{
		return func.getTitle();
	}
	public void setParameter(int i, double d) throws InvalidFunctionParameter
	{
		if (fitterParameters.length == functionParameters.length)
			func.setParameter(i, d);
		else
			for (int j = 0; j <= i; j++) if (!inFit[j]) i++;
		func.setParameter(i, d);
		/*
		 * The basic idea here (it's probably not
		 * obvious) is that i is the parameter
		 * taken from the shortened list.  It
		 * must be increased by one for every
		 * parameter that is not in the fit
		 * and is below where it will eventually
		 * be.
		 */
	}
	public double valueAt(double d) throws FunctionValueUndefined
	{
		return func.valueAt(d);
	}

}
