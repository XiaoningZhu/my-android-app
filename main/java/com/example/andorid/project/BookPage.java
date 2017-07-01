package com.example.andorid.project;

import java.text.DecimalFormat;
import java.util.Vector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.format.Time;
import android.util.Log;

/**
 * 这个类的目的是为在看书翻页时，需要进行的动作提供接口。
 * 包括翻向下一页，翻向上一页。在翻到每章最后一页时，如果后面还有章节就继续翻向下一章节，没有就向用户显示已读完。
 * 在翻向上一章节时，如果前面还有章节，就翻到上一章节，没有就向用户显示，这已经是第一章节。
 *
 * 在直觉上认为这个应该只设置成一个接口，因为只需向视图层提供动作接口，也就是本类应属于模型层。则其设置为一个借口可能也合适。
 * 但是如果设置成一个接口，那么接口的实现类，有多个都要保存的数据。那么为了代码重用，抽象类可能比接口更加合适。 上面是个人分析，可能不是很合适。
 *
 * @author MJZ
 *
 */
public class BookPage{
    myDB db;
    // configuration information
    private int screenWidth; // 屏幕宽度
    private int screenHeight; // 屏幕高度
    private int fontSize; // 字体大小
    private int lineHgight;	//每行的高度
    private int marginWidth = 15; // 左右与边缘的距离
    private int marginHeight = 15; // 上下与边缘的距离
    private int textColor; // 字体颜色
    private Bitmap bgBitmap; // 背景图片
    private int bgColor; // 背景颜色

    private Paint paint;
    private Paint paintBottom;
    private int visibleWidth; // 屏幕中可显示文本的宽度
    private int visibleHeight;
    private Chapter chapter; // 需要处理的章节对象
    private Vector<String> linesVe; // 将章节內容分成行，并将每页按行存储到vector对象中
    private int lineCount; // 一个章节在当前配置下一共有多少行

    private String content;
    private int chapterLen; // 章节的长度
    // private int curCharPos; // 当前字符在章节中所在位置
    private int charBegin; // 每一页第一个字符在章节中的位置
    private int charEnd; // 每一页最后一个字符在章节中的位置
    private boolean isfirstPage;
    private boolean islastPage;

    private Vector<Vector<String>> pagesVe;
    int pageNum;
    private int totNumChapt = 0;

    /**
     * 在新建一个BookPage对象时，需要向其提供数据，以支持屏幕翻页功能。
     *
     * @param screenWidth
     *            屏幕宽度，用来计算每行显示多少字
     * @param screenHeight
     *            屏幕高度，用来计算每页显示多少行
     * @param chapter
     *            章节对象
     */
    public BookPage(int screenWidth, int screenHeight, Chapter chapter, myDB db) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.chapter = chapter;
        this.db  = db;
        init();//////////////////////////////////
    }

    /**
     * 初始最好按照定义变量的顺序来初始化，统一。在将来需要修改某个变量的时候，容易找到。 对代码维护应该也很有用吧。
     */
    protected void init() {
        bgBitmap = null;
        bgColor = 0xffff9e85;
        textColor = Color.BLACK;

        int order = chapter.getOrder();
        content = db.queryByPosition(order);
        Log.i("gg", content);
        //content = "eeeeeeeeeeeee\n";
        chapterLen = content.length();
        // curCharPos = 0;
        charBegin = 0;
        charEnd = 0;
        fontSize = 40;
        lineHgight = fontSize + 8;
        linesVe = new Vector<String>();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Align.LEFT);
        paint.setTextSize(fontSize);
        paint.setColor(textColor);

        paintBottom = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBottom.setTextAlign(Align.LEFT);
        paintBottom.setTextSize(fontSize / 2);
        paintBottom.setColor(textColor);

        visibleWidth = screenWidth - marginWidth * 2;
        visibleHeight = screenHeight - marginHeight * 2;
        lineCount = visibleHeight / lineHgight - 2;
        isfirstPage = true;
        islastPage = false;
        pagesVe = new Vector<Vector<String>>();
        pageNum = -1;
        totNumChapt = db.get_total_number();
        /////////////////////////////////////////////
        slicePage();
    }
    public void setLtextSize()
    {
        if(fontSize <= 75)
            fontSize = fontSize+3;
        lineHgight = fontSize + 8;
        lineCount = visibleHeight / lineHgight - 2;
        paint.setTextSize(fontSize);
        slicePage();
    }

    public void setStextSize()
    {
        if(fontSize >= 15)
            fontSize = fontSize-3;
        lineHgight = fontSize + 8;
        lineCount = visibleHeight / lineHgight - 2;
        paint.setTextSize(fontSize);
        slicePage();
    }


    public Vector<String> getCurPage() {
        return linesVe;
    }

    protected void slicePage() {
        pagesVe.clear();
        int curPos = 0;
        while (curPos < chapterLen) {
            Vector<String> lines = new Vector<String>();
            charBegin = curPos;
            while (lines.size() < lineCount && curPos < chapterLen) {
                int i = content.indexOf("\n", curPos);

                String paragraphStr = content.substring(curPos, i);///////////////////////////////////////////
                // curCharPos += i;
                if (curPos == i)
                    lines.add("");

                while (paragraphStr.length() > 0) {
                    int horSize = paint.breakText(paragraphStr, true,
                            visibleWidth, null);
                    lines.add(paragraphStr.substring(0, horSize));
                    paragraphStr = paragraphStr.substring(horSize);
                    curPos += horSize;
                    if (lines.size() > lineCount)
                        break;
                }
                // 如果是把一整段读取完的话，需要给当前位置加1
                if (paragraphStr.length() == 0)
                    curPos += "\n".length();
            }
            pagesVe.add(lines);
        }

    }

    /**
     * 翻到下一页
     */
    public boolean nextPage() {
        if (isLastPage()) {
            if (!nextChapter()) // 如果已经到本书末尾，那么不能继续执行翻页代码
                return false;
        }

        linesVe = pagesVe.get(++pageNum);
        return true;
    }
    /**
     * 翻到上一页
     */
    public boolean prePage() {
        if (isFirstPage()) {
            if (!preChapter()) // 如果已经到本书第一章，就不能继续执行翻页代码
            {
                Log.i("tttttttttttt", "ttttttttttttt");
                return false;

            }
        }

        linesVe = pagesVe.get(--pageNum);
        return true;
    }

    /**
     * 跳到下一章，若返回值为false，则当前章节已经为最后一章
     */
    public boolean nextChapter() {
        int order = chapter.getOrder();
        if(order+1 == totNumChapt)
            return false;
        else {
            chapter.setOrder(order+1);
            int order1 = order+2;
            chapter.setTitle("第"+order1+"章");
            String next_content =db.queryByPosition(order + 1);
            content = next_content;

            chapterLen = content.length();
            // curCharPos = 0;
            charBegin = 0;
            charEnd = 0;
            slicePage();
            pageNum = -1;
            return true;
        }

    }

    /**
     * 跳到上一章,若返回值为false，则当前章节已经为第一章
     */
    public boolean preChapter() {
        int order = chapter.getOrder();
        if(order == 0)
            return false;
        else {
            chapter.setOrder(order-1);
            chapter.setTitle("第"+order+"章");
            String next_content =db.queryByPosition(order- 1);
            if (next_content == null)
                return false;
            //chapter = tempChapter;
            content = next_content;
            chapterLen = content.length();
            // curCharPos = chapterLen;
            charBegin = chapterLen;
            charEnd = chapterLen;
            slicePage();
            pageNum = pagesVe.size();
            return true;
        }

    }

    public boolean isFirstPage() {
        if (pageNum <= 0)
            return true;
        return false;
    }

    public boolean isLastPage() {
        if (pageNum >= pagesVe.size() - 1)
            return true;
        return false;
    }

    public void draw(Canvas c) {
        if (linesVe.size() == 0)
            nextPage();
        if (linesVe.size() > 0) {
            if (bgBitmap == null)
                c.drawColor(bgColor);
            else
                c.drawBitmap(bgBitmap, 0, 0, null);

            int y = marginHeight;
            for (String line : linesVe) {
                y += lineHgight;
                c.drawText(line, marginWidth, y, paint);
            }
        }

        int order = chapter.getOrder();
        float percent = (float)order/totNumChapt;
        DecimalFormat df = new DecimalFormat("#0.0");
        String percetStr = df.format(percent * 100) + "%";

        Time time = new Time();
        time.setToNow();
        String timeStr;
        if (time.minute < 10)
            timeStr = "" + time.hour + " : 0" + time.minute;
        else
            timeStr = "" + time.hour + " : " + time.minute;

        int pSWidth = (int) paintBottom.measureText("99.9%") + 2;
        int titWidth = (int) paintBottom.measureText(chapter.getTitle());


        c.drawText(timeStr, marginWidth / 2, screenHeight - 5, paintBottom);
        c.drawText(chapter.getTitle(), screenWidth / 2 - titWidth / 2,
                screenHeight - 5, paintBottom);
        c.drawText(percetStr, screenWidth - pSWidth, screenHeight - 5, paintBottom);
    }

    public void setBgBitmap(Bitmap bMap) {
        bgBitmap = Bitmap.createScaledBitmap(bMap, screenWidth, screenHeight,
                true);
    }

    public void setPageNum(double percent)
    {
        int order = (int)(Math.floor(percent*totNumChapt));
        chapter.setOrder(order);
        int order1 = order+1;
        chapter.setTitle("第"+order1+"章");
        String jump_content =db.queryByPosition(order);
        if (jump_content == null)
            return;
        //chapter = tempChapter;
        content = jump_content;
        chapterLen = content.length();
        // curCharPos = chapterLen;
        charBegin = chapterLen;
        charEnd = chapterLen;
        slicePage();
        pageNum = pagesVe.size();

    }

    public void setPageNum1(int pagenum)
    {
        pageNum = pagenum;
        linesVe = pagesVe.get(pageNum);
    }

    public int getPageNum()
    {
        return  pageNum;
    }

}


