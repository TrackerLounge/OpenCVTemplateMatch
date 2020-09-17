[Home Page](https://github.com/TrackerLounge/Home)

# OpenCV Template Match
Explore OpenCV's matchTemplates() method, to find similar images in a scene image. Draw a bounding box around all matching images above a given threshold.
Experiment with rotating and scaling the template to find variations in the scene. Extract these matches and align them all so they share the same degree of rotation and can be compared side by side.

# Youtube Video
[![Alt text](https://github.com/TrackerLounge/OpenCVTemplateMatch/blob/master/resources/slashScreen_small.jpg)](https://www.youtube.com/watch?v=gAdhyWBD1Dc)

# Text of Video

Explore OpenCV's matchTemplates() method, to find similar images in a scene image. Draw a bounding box around all matching images above a given threshold. Experiment with rotating and scaling the template to find variations in the scene. Extract these matches and align them all so they share the same degree of rotation and can be compared side by side.

We have an image of a track. 
We will call this our template image.
We also have an image of multiple tracks.
We will call this our scene image.
We want some code to look at the scene image and tell us if it sees anything that looks like our template image

As it so happens, OpenCV allows us to do this pretty simply.
Here is a rough program to do that.

We need to:
Define the Match Method – Line 52
Define where to store the result – Line 53
Run the MatchTemplate() – Line 54
Get the match result (max/min) – Line 55
Get the location of the match – Line 56
Draw a rectangle around the match in the scene – Line 57-58

Here is the result. The image is in grayscale so the box 
around the match is drawn in white.

On Line 52, we define the Match Method. 
There are 6 choices.
In TM_SQDIFF, the best match has the lowest value.
In the other five options, the best match has the highest value  
Effects line 56 – mmr.maxLoc or mmr.minLoc
For a description of each see the link below
https://docs.opencv.org/2.4/doc/tutorials/imgproc/histograms/template_matching/template_matching.html

But what about the other matches?

In my search, I’ve found two options:
1. Black out the found result and search again – (seems a bit silly)
2. Use code to return all results above a threshold

We will look at both options

We find the first match and black it out

We search again to find 
the second match.

We add the first result image to the second result image using BITWISE_OR()

Here is some rough sample code to implement this approach.
You can see on line 56 we run the first template match and on line 74 we run the second template match
On line 66-72 we draw a black filled rectangle over the first match to exclude it from our second search.
You could imagine putting this into a while look and running until no results are found.
The code is simple and very inefficient.
If two potential matches are very close together, we risk hiding an unfound match.

Option 2: Return all results above a threshold
Code Source: Github -  openpnp / openpnp 
https://github.com/openpnp/openpnp/blob/develop/src/main/java/org/openpnp/vision/pipeline/stages/MatchPartTemplate.java#L283

https://github.com/openpnp/openpnp/blob/develop/src/main/java/org/openpnp/util/OpenCvUtils.java

https://github.com/openpnp/openpnp/blob/develop/src/main/java/org/openpnp/vision/pipeline/CvStage.java

* No affiliation with this Github project.

Steps:
Run the matchTemplate() and store results in outputImage
Review each row of the outputImage, looking for any matches greater than the threshold. Add matches above threshold to matMaxima list.
For each match in matMaxima list, draw a rectangle in the scene

Note: I did not checkout the entire Github project. I copied and pasted these code fragments into my Main.java class and used them there.
This second approach is a bit more complicated than the first approach. I think the added complexity is worth it in this case.
I think this second approach is much better than the first approach.
It is more efficient because we are running the matchTemplate() once rather than again and again.
It requires only one copy of the original scene image, rather than multiple copies.

How similar does the scene need to be to the template?

How does it handle scale and rotation differences? 

Test Image:
1. Same as Template
2. Toe region showing outline only
3. Toe and heal are partially occluded
4. Rotated 30 degrees
5. Very faint outline
6. Scaled up 20+ pixels

At Threshold: 0.7, the matchTemplate() found the exact copy of the template (#1) and the slightly larger version (#6).

With a lower threshold to 0.6, we also find the partially occluded track (#3).

As the threshold gets lower, we begin to get bad results.

If the threshold is too low, the error rate becomes very high.

Could we rotate and/or scale the template and search again?

We have to be a little careful with rotation.
OpenCV does not resize the image and will loose info

Before we rotate our image, we need to resize.
We make it a square image with each side equal to the original image’s diagonal length.

I moved the prep and rotation logic into a utility class.

matchTemplate() now finds the track rotated by 30 degrees

To scale the image, we can do this:

But it is a little unwieldy for scaling proportionally by a few pixels.

We can proportionally scale to a desired pixel width.
This allows us to scale up our template by a few pixels at a time

We can move scaling logic into a utility class.

Now, the matchTemplate() finds a match in the lower right corner.

We could put the scale and rotations together.
Say “Look for all instances of this template where the width is between 95 and 130 pixels and where the template can be rotated 0 to 90 degrees.”

Even on a small scene image, this can take 30 seconds+ or much longer. We also get duplicate matches. 

To speed up the process, depending on your data, you could:
Work with smaller copies of template and scene (1/4, or 1/8 scale) – fewer pixels = less work
Check a smaller range of rotation degrees
Change the for loop to increment by 5 degrees rather than 1 degree, etc.
Check a smaller range of scale pixel widths
Change the loop to increment by 5 pixels rather than 1 pixel, etc.

To avoid duplicates, we could:
Mask found results before running matchTemplate().
Risk partially or fully hiding other real matches.
Try to identify overlapping matches.
Lots of subtle problems start to emerge.
Neither one is risk free.

Can we extract matches and rotate them into common alignment?

At threshold 0.7 we find duplicate results, and incorrect results.

At threshold 0.8, we get accurate results, but still have duplicates.

Here is the list of found matches. Most are duplicates.

In order to do extract matches and rotate them into common alignment, we need to:
Find matches and remember the templates current degree of rotation
Remove duplicates
Extract a Region of Interest or ROI
Rotate the ROI by some amount of degrees

Note: I will move some tasks into their own classes and functions

In the Main Class, rotate template and call search()

In Main.search() - Find Matches, record rotation degrees

I modified the TemplateMatch Class to store the template degree of rotation.

I added a method to remove duplicate matches.

I created a Helper Method to Get Region of Interest (ROI)

In the Main Class, we remove duplicates, draw bounding boxes, extract ROI images, and rotate the ROIs to have uniform alignment.

Here are the list of matches, after removing duplicate.
Now all the resulting matches have a common alignment.

Download code from:

https://github.com/TrackerLounge/OpenCVTemplateMatch

Conclusions:
The OpenCV matchTemplates() is pretty powerful and can be made more powerful with a few additional methods.
Thresholding can be a challenge resulting in bad / duplicate matches.
It can be very SLOW and processor intensive!
Reframe the problem to reduce the range of variation where possible.
Work with the smallest images that retain significant details.
As with just about all Computer Vision approaches, it may not be a good fit in all cases, particularly in problems where answer is required in real-time.

# Environment
This code was written in Eclipse using Java 1.8.0.101
This code requires that you include opencv jar in your class path.

# References

This code makes use of code taken from:

https://docs.opencv.org/master/de/da9/tutorial_template_matching.html

https://riptutorial.com/opencv/example/22915/template-matching-with-java

https://github.com/openpnp/openpnp/blob/develop/src/main/java/org/openpnp/vision/pipeline/stages/MatchPartTemplate.java#L283

https://github.com/openpnp/openpnp/blob/develop/src/main/java/org/openpnp/util/OpenCvUtils.java

https://github.com/openpnp/openpnp/blob/develop/src/main/java/org/openpnp/vision/pipeline/CvStage.java

