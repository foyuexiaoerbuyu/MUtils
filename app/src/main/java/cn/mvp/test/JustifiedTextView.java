package cn.mvp.test;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* ***********************************************************************

Copyright 2014 CodesGood

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*********************************************************************** */
//Created by CodesGood on 7/12/14.

public class JustifiedTextView extends AppCompatTextView {
    //将填充空格之间的空格的头发空间字符。
    private final static String HAIR_SPACE = "\u200A";

    //在单词之间的位置将有一个正常的空格字符。
    private final static String NORMAL_SPACE = " ";

    //TextView的宽度。
    private int viewWidth;

    //TextView文本中的对齐句子。
    private List<String> sentences = new ArrayList<>();

    //正在对齐的句子。
    private List<String> currentSentence = new ArrayList<>();

    //填充空格的句子。
    private List<String> sentenceWithSpaces = new ArrayList<>();

    //将存储插入空格的文本的字符串。
    private String justifiedText = "";

    //生成随机数的对象，这是对齐算法的一部分。
    private Random random = new Random();

    //默认的构造函数。
    public JustifiedTextView(Context context) {
        super(context);
    }

    public JustifiedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JustifiedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //如果已经完成，此类不会重复对齐文本的过程。
        if (!justifiedText.equals(getText().toString())) {

            ViewGroup.LayoutParams params = getLayoutParams();
            String text = getText().toString();
            viewWidth = (int) (getPaint().measureText("汉") * 7) - (getPaddingLeft() + getPaddingRight());
//            viewWidth = getMeasuredWidth() - (getPaddingLeft() + getPaddingRight());

            //如果TextView的宽度为wrap_content，此类将不会对齐文本
            //并且不会对齐文本，如果视图宽度为0
            //并且！如果文本为空，则不会对齐文本。
            if (/*params.width != ViewGroup.LayoutParams.WRAP_CONTENT &&*/ viewWidth > 0 && !text.isEmpty()) {
                justifiedText = getJustifiedText(text);

                if (!justifiedText.isEmpty()) {
                    setText(justifiedText);
                    sentences.clear();
                    currentSentence.clear();
                }
            } else {
                super.onDraw(canvas);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    /**
     * 获取一个带有适当空格的字符串，以在TextView中对齐文本。
     *
     * @param text 要对齐的文本
     * @return 对齐的文本
     */
    private String getJustifiedText(String text) {
        String[] words = text.split(NORMAL_SPACE);

        for (String word : words) {
            boolean containsNewLine = (word.contains("\n") || word.contains("\r"));

            if (fitsInSentence(word, currentSentence, true)) {
                addWord(word, containsNewLine);
            } else {
                sentences.add(fillSentenceWithSpaces(currentSentence));
                currentSentence.clear();
                addWord(word, containsNewLine);
            }
        }

        //确保我们添加最后一句（如果需要）。
        if (currentSentence.size() > 0) {
            sentences.add(getSentenceFromList(currentSentence, true));
        }

        //返回对齐的文本。
        return getSentenceFromList(sentences, false);
    }

    /**
     * 将单词添加到句子中，并在“新行”是字符串的一部分时开始新句子。
     *
     * @param word            要添加的单词
     * @param containsNewLine 指定字符串是否包含新行
     */
    private void addWord(String word, boolean containsNewLine) {
        currentSentence.add(word);
        if (containsNewLine) {
            sentences.add(getSentenceFromListCheckingNewLines(currentSentence));
            currentSentence.clear();
        }
    }

    /**
     * 使用列表中的单词创建一个字符串，并在需要时在单词之间添加空格。
     *
     * @param strings   要合并为一个的字符串
     * @param addSpaces 指定方法是否应在单词之间添加空格。
     * @return 返回使用列表中的单词的句子。
     */
    private String getSentenceFromList(List<String> strings, boolean addSpaces) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String string : strings) {
            stringBuilder.append(string);

            if (addSpaces) {
                stringBuilder.append(NORMAL_SPACE);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 使用列表中的单词创建一个字符串，并在单词之间添加空格，并考虑到新行。
     *
     * @param strings 要合并为一个的字符串
     * @return 返回使用列表中的单词的句子。
     */
    private String getSentenceFromListCheckingNewLines(List<String> strings) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String string : strings) {
            stringBuilder.append(string);

            //我们不想在单词旁边添加空格，如果它包含一个新行字符
            if (!string.contains("\n") && !string.contains("\r")) {
                stringBuilder.append(NORMAL_SPACE);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 用适当数量的空格填充句子。
     *
     * @param sentence 我们将用来构建带有附加空格的句子的句子
     * @return 带有空格的字符串。
     */
    private String fillSentenceWithSpaces(List<String> sentence) {
        sentenceWithSpaces.clear();

        //如果收到的句子是一个单词，我们不需要做这个过程。
        if (sentence.size() > 1) {
            //我们首先用普通空格填充，因为我们可以确信"fitsInSentence"
            //已经考虑到了这些空格。
            for (String word : sentence) {
                sentenceWithSpaces.add(word);
                sentenceWithSpaces.add(NORMAL_SPACE);
            }

            //用细空格填充句子。
            while (fitsInSentence(HAIR_SPACE, sentenceWithSpaces, false)) {
                //我们从句子大小中减去2，因为我们需要确保我们没有在
                //行的末尾添加空格。
                sentenceWithSpaces.add(getRandomNumber(sentenceWithSpaces.size() - 2), HAIR_SPACE);
            }
        }

        return getSentenceFromList(sentenceWithSpaces, false);
    }

    /**
     * 验证要添加的单词是否适合在句子中
     *
     * @param word      要添加的单词
     * @param sentence  将接收新单词的句子
     * @param addSpaces 指定我们是否应该添加空格进行验证
     * @return 如果单词适合，返回true，否则返回false。
     */
    private boolean fitsInSentence(String word, List<String> sentence, boolean addSpaces) {
        String stringSentence = getSentenceFromList(sentence, addSpaces);
        stringSentence += word;

        float sentenceWidth = getPaint().measureText(stringSentence);

        return sentenceWidth < viewWidth;
    }

    /**
     * 返回一个随机数，这是算法的一部分...不要怪我。
     *
     * @param max 范围内的最大数
     * @return 随机数。
     */
    private int getRandomNumber(int max) {
        //我们将1加到结果中，因为我们想要防止逻辑在
        //句子的开头添加空格。
        return random.nextInt(max) + 1;
    }
}