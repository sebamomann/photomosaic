# Photomosaic
## Usage
### Preparation
Create a ``img``  folder within the project root path. Paste all source images you want to have in the mosiac in here.
Adapt the configuration in ``de.sebamomann.config/config.cfg`` or when executing a jar file adapt it in the same folder as the jar file is located

### Variable explanation config.cfg
```
# number of thread to preprocess images
threadsPrepare = 10
# number of threads to build final image
threadsBuild = 25

# width and height in pixels of the sub images
subImgSize = 10
# dimensions of final image (e.g. 50 images wide and 50 images high)
goalImgSizeX = 50
goalImgSizeY = 50

# color deviation to start with
beginningDeviation = 0
# increase deviation by x, when there are no images with given deviation found
deviationIncrease = 3

# Update console every x seconds
intervallIndex = 1
``` 
## Heap space
Make sure you give enough heap space.  If exported as ``.jar`` file you can set the heap space via ``.bat`` file
For example
```
@echo on
java -Xmx14336M -Xms14336M -jar mosaic.jar
pause
```
With ``Xmx`` being the maximum heap space and ``Xms`` being the standard heap space
In this example the available heap space is set to 14GB
