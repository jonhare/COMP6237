package uk.ac.soton.ecs.comp6237.l3;

public class RGBData extends ItemTermData {
	public static RGBData threeColors() {
		final RGBData rgb = new RGBData();
		rgb.setCounts(new float[][] {
				{
					255, 0, 0
				}, {
					0, 255, 0
				}, {
					0, 0, 255
				}
		});

		rgb.getItemNames().clear();
		rgb.getItemNames().add("Red");
		rgb.getItemNames().add("Green");
		rgb.getItemNames().add("Blue");

		rgb.getTerms().clear();
		rgb.getTerms().add("R");
		rgb.getTerms().add("G");
		rgb.getTerms().add("B");

		return rgb;
	}

	public static RGBData eightColors() {
		final RGBData rgb = new RGBData();
		rgb.setCounts(new float[][] {
				{ 0, 0, 0 }, { 255, 0, 0 }, { 0, 255, 0 }, { 0, 0, 255 },
				{ 255, 255, 0 }, { 0, 255, 255 }, { 255, 0, 255 }, { 255, 255, 255 }
		});

		rgb.getItemNames().clear();
		rgb.getItemNames().add("0 0 0");
		rgb.getItemNames().add("1 0 0");
		rgb.getItemNames().add("0 1 0");
		rgb.getItemNames().add("0 0 1");
		rgb.getItemNames().add("1 1 0");
		rgb.getItemNames().add("0 1 1");
		rgb.getItemNames().add("1 0 1");
		rgb.getItemNames().add("1 1 1");

		rgb.getTerms().clear();
		rgb.getTerms().add("R");
		rgb.getTerms().add("G");
		rgb.getTerms().add("B");

		return rgb;
	}
}
