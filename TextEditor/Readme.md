# Description
### In this stage, you should implement various other scientific approaches to calculate a readability score:

You can look at different ages corresponding to the different scores by the table in [this](https://en.wikipedia.org/wiki/Automated_readability_index) article. This table is suitable for all the algorithms described in this stage. To calculate the age use upper bound of the range. For example, if the range is *12-13 year olds* then it's upper bound is *13 year olds*.

The first one is **Flesch–Kincaid** readability tests. To calculate [this](https://en.wikipedia.org/wiki/Flesch%E2%80%93Kincaid_readability_tests), you also need to create a method that calculates a number of syllables in a word. The formula is listed below. You can learn more here. You can use the second formula to calculate the index - it allows you to easily calculate the age of a person using the same table from the Automated Readability Index.

The second one is **SMOG index**. It stands for Simple Measure of Gobbledygook. To calculate this, you need to count the number of polysyllables which is the number of words with more than 2 syllables. The formula is shown below. You can find out more [here](https://en.wikipedia.org/wiki/SMOG). The Wikipedia page says that at least 30 sentences are required for this index to work properly. Don't pay attention to this, just keep it in mind when you use this index in real life. As in the previous example, the grade level is calculated here, so to get the age of a person you need to use the table from the first link.

The next one is **Coleman–Liau index**. The formula is shown below. You can find out more [here](http://en.wikipedia.org/wiki/Coleman%E2%80%93Liau_index). L is the average number of characters per 100 words and S is the average number of sentences per 100 words. Like all other indexes, the output is a person's grade level. Like all other indexes, the result is a minimum grade level of the person needed to understand this text.

So, in this stage, you should program all three approaches. Don't forget about the **Automated readability index** - it should also be here. Also, there should be an option to choose all methods at the same time.

To count the number of syllables you should use letters *a, e, i, o, u, y* as vowels. You can see [here](https://simple.wikipedia.org/wiki/Vowel) examples and difficulties with determining vowels in a word with 100% accuracy. So, let's use the following 4 rules:

1. Count the number of vowels in the word.
2. Do not count double-vowels (for example, "rain" has 2 vowels but is only 1 syllable)
3. If the last letter in the word is 'e' do not count it as a vowel (for example, "side" is 1 syllable) 
4. If at the end it turns out that the word contains 0 vowels, then consider this word as 1-syllable.


