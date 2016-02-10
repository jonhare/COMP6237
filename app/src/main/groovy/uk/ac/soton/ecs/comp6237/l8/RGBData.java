package uk.ac.soton.ecs.comp6237.l8;

public class RGBData extends BlogData {
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

		rgb.getBlogNames().clear();
		rgb.getBlogNames().add("Red");
		rgb.getBlogNames().add("Green");
		rgb.getBlogNames().add("Blue");

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

		rgb.getBlogNames().clear();
		rgb.getBlogNames().add("0 0 0");
		rgb.getBlogNames().add("1 0 0");
		rgb.getBlogNames().add("0 1 0");
		rgb.getBlogNames().add("0 0 1");
		rgb.getBlogNames().add("1 1 0");
		rgb.getBlogNames().add("0 1 1");
		rgb.getBlogNames().add("1 0 1");
		rgb.getBlogNames().add("1 1 1");

		rgb.getTerms().clear();
		rgb.getTerms().add("R");
		rgb.getTerms().add("G");
		rgb.getTerms().add("B");

		return rgb;
	}
}
