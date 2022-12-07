gource \
  -1920x1080 \
  --highlight-users \
  --hide mouse,filenames \
  -o - \
| ffmpeg \
  -y \
  -r 30 \
  -f image2pipe \
  -vcodec ppm \
  -i - \
  -vcodec libx264 \
  -pix_fmt yuv420p \
  -crf 1 \
  -threads 0 \
  -bf 0 input.mp4 \
&& ffmpeg \
  -y \
  -i input.mp4 \
  -filter:v "setpts=PTS/2" output.mp4 \
&& rm -f ./input.mp4
