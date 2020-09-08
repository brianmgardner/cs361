Miles Chandler & Brian Gardner
03/14/20

Questions:
1.) Comparing your original and modified images carefully, can you detect *any* difference visually (that is, in the appearance of the image)?
    No, the two images look practically identical.
2.) Can you think of other ways you might hide the message in image files (or in other types of files)?
    Using the ends of bits to symbolize 1/0 bits could work with an audio file or a video file while still being undetectable to the naked eye.
3.)  Can you invent ways to increase the bandwidth of the channel?
    The input text could have been compressed with Huffman encoding to allows for more information to be hidden.
4.) Suppose you were tasked to build an "image firewall" that would block images containing hidden messages. Could you do it? How might you approach this problem?
    You could scan the image, and make the ending values for RGB all even or all odd.
5.) Does this fit our definition of a covert channel? Explain your answer.
    Yes, this allows for information to be passed in a way that was not intended.
