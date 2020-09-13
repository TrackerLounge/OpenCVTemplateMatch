package org.trackerlounge;

/* Code extracted from:
 * https://github.com/openpnp/openpnp/blob/develop/src/main/java/org/openpnp/vision/pipeline/CvStage.java
*/
public class TemplateMatch {
	public double x;
	public double y;
	public double width;
	public double height;
	public double score;
	public double templateRotatedByDegree; //I added this

	public TemplateMatch(double x, double y, double width, double height, double score, double templateRotatedByDegree) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.score = score;
		this.templateRotatedByDegree = templateRotatedByDegree;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getTemplateRotatedByDegree() {
		return templateRotatedByDegree;
	}

	public void setTemplateRotatedByDegree(double templateRotatedByDegree) {
		this.templateRotatedByDegree = templateRotatedByDegree;
	}

	@Override
	public String toString() {
		return "TemplateMatch [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + ", score=" + score +", templateRotatedByDegree: "+templateRotatedByDegree
				+ "]";
	}
}