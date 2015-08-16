/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eventer.app.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.eventer.app.R;

public class SmileUtils {
	public static final String e_1 = "[):]";
	public static final String e_2 = "[:D]";
	public static final String e_3 = "[;)]";
	public static final String e_4 = "[:-o]";
	public static final String e_5 = "[:p]";
	public static final String e_6 = "[(H)]";
	public static final String e_7 = "[:@]";
	public static final String e_8 = "[:s]";
	public static final String e_9 = "[:$]";
	public static final String e_10 = "[:(]";
	public static final String e_11 = "[:'(]";
	public static final String e_12 = "[:|]";
	public static final String e_13 = "[(a)]";
	public static final String e_14 = "[8o|]";
	public static final String e_15 = "[8-|]";
	public static final String e_16 = "[+o(]";
	public static final String e_17 = "[<o)]";
	public static final String e_18 = "[|-)]";
	public static final String e_19 = "[*-)]";
	public static final String e_20 = "[:-#]";
	public static final String e_21 = "[:-*]";
	public static final String e_22 = "[^o)]";
	public static final String e_23 = "[8-)]";
	public static final String e_24 = "[(|)]";
	public static final String e_25 = "[(u)]";
	public static final String e_26 = "[(S)]";
	public static final String e_27 = "[(*)]";
	public static final String e_28 = "[(#)]";
	public static final String e_29 = "[(R)]";
	public static final String e_30 = "[({)]";
	public static final String e_31 = "[(})]";
	public static final String e_32 = "[(k)]";
	public static final String e_33 = "[(F)]";
	public static final String e_34 = "[(W)]";
	public static final String e_35 = "[(D)]";
	public static final String e_36 = "[e_36]";
	public static final String e_37 = "[e_37]";
	public static final String e_38 = "[e_38]";
	public static final String e_39 = "[e_39]";
	public static final String e_40 = "[e_40]";
	public static final String e_41 = "[e_41]";
	public static final String e_42 = "[e_42]";
	public static final String e_43 = "[e_43]";
	public static final String e_44 = "[e_44]";
	public static final String e_45 = "[e_45]";
	public static final String e_46 = "[e_46]";
	public static final String e_47 = "[e_47]";
	public static final String e_48 = "[e_48]";
	public static final String e_49 = "[e_49]";
	public static final String e_50 = "[e_50]";
	public static final String e_51 = "[e_51]";
	public static final String e_52 = "[e_52]";
	public static final String e_53 = "[e_53]";
	public static final String e_54 = "[e_54]";
	
	
	private static final Factory spannableFactory = Spannable.Factory
	        .getInstance();
	
	private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

	static {
		
	    addPattern(emoticons, e_1, R.drawable.e_1);
	    addPattern(emoticons, e_2, R.drawable.e_2);
	    addPattern(emoticons, e_3, R.drawable.e_3);
	    addPattern(emoticons, e_4, R.drawable.e_4);
	    addPattern(emoticons, e_5, R.drawable.e_5);
	    addPattern(emoticons, e_6, R.drawable.e_6);
	    addPattern(emoticons, e_7, R.drawable.e_7);
	    addPattern(emoticons, e_8, R.drawable.e_8);
	    addPattern(emoticons, e_9, R.drawable.e_9);
	    addPattern(emoticons, e_10, R.drawable.e_10);
	    addPattern(emoticons, e_11, R.drawable.e_11);
	    addPattern(emoticons, e_12, R.drawable.e_12);
	    addPattern(emoticons, e_13, R.drawable.e_13);
	    addPattern(emoticons, e_14, R.drawable.e_14);
	    addPattern(emoticons, e_15, R.drawable.e_15);
	    addPattern(emoticons, e_16, R.drawable.e_16);
	    addPattern(emoticons, e_17, R.drawable.e_17);
	    addPattern(emoticons, e_18, R.drawable.e_18);
	    addPattern(emoticons, e_19, R.drawable.e_19);
	    addPattern(emoticons, e_20, R.drawable.e_20);
	    addPattern(emoticons, e_21, R.drawable.e_21);
	    addPattern(emoticons, e_22, R.drawable.e_22);
	    addPattern(emoticons, e_23, R.drawable.e_23);
	    addPattern(emoticons, e_24, R.drawable.e_24);
	    addPattern(emoticons, e_25, R.drawable.e_25);
	    addPattern(emoticons, e_26, R.drawable.e_26);
	    addPattern(emoticons, e_27, R.drawable.e_27);
	    addPattern(emoticons, e_28, R.drawable.e_28);
	    addPattern(emoticons, e_29, R.drawable.e_29);
	    addPattern(emoticons, e_30, R.drawable.e_30);
	    addPattern(emoticons, e_31, R.drawable.e_31);
	    addPattern(emoticons, e_32, R.drawable.e_32);
	    addPattern(emoticons, e_33, R.drawable.e_33);
	    addPattern(emoticons, e_34, R.drawable.e_34);
	    addPattern(emoticons, e_35, R.drawable.e_35);
	    addPattern(emoticons, e_36, R.drawable.e_36);
	    addPattern(emoticons, e_37, R.drawable.e_37);
	    addPattern(emoticons, e_38, R.drawable.e_38);
	    addPattern(emoticons, e_39, R.drawable.e_39);
	    addPattern(emoticons, e_40, R.drawable.e_40);
	    addPattern(emoticons, e_41, R.drawable.e_41);
	    addPattern(emoticons, e_42, R.drawable.e_42);
	    addPattern(emoticons, e_43, R.drawable.e_43);
	    addPattern(emoticons, e_44, R.drawable.e_44);
	    addPattern(emoticons, e_45, R.drawable.e_45);
	    addPattern(emoticons, e_46, R.drawable.e_46);
	    addPattern(emoticons, e_47, R.drawable.e_47);
	    addPattern(emoticons, e_48, R.drawable.e_48);
	    addPattern(emoticons, e_49, R.drawable.e_49);
	    addPattern(emoticons, e_50, R.drawable.e_50);
	    addPattern(emoticons, e_51, R.drawable.e_51);
	    addPattern(emoticons, e_52, R.drawable.e_52);
	    addPattern(emoticons, e_53, R.drawable.e_53);
	    addPattern(emoticons, e_54, R.drawable.e_54);
	}

	private static void addPattern(Map<Pattern, Integer> map, String smile,
	        int resource) {
	    map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}

	/**
	 * replace existing spannable with smiles
	 * @param context
	 * @param spannable
	 * @return
	 */
	public static boolean addSmiles(Context context, Spannable spannable) {
	    boolean hasChanges = false;
	    for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(spannable);
	        while (matcher.find()) {
	            boolean set = true;
	            for (ImageSpan span : spannable.getSpans(matcher.start(),
	                    matcher.end(), ImageSpan.class))
	                if (spannable.getSpanStart(span) >= matcher.start()
	                        && spannable.getSpanEnd(span) <= matcher.end())
	                    spannable.removeSpan(span);
	                else {
	                    set = false;
	                    break;
	                }
	            if (set) {
	                hasChanges = true;
	                Drawable drawable = context.getResources().getDrawable(entry.getValue());  
	                int scale=spTopx(context, 22);
	                drawable.setBounds(0, 0, scale, scale);//这里设置图片的大小   
	                ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);   
	                spannable.setSpan(imageSpan,
	                        matcher.start(), matcher.end(),
	                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	            }
	        }
	    }
	    return hasChanges;
	}

	 public static int spTopx(Context context, float spValue) { 
	        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
	        return (int) (spValue * fontScale + 0.5f); 
	    } 
	
	public static Spannable getSmiledText(Context context, CharSequence text) {
	    Spannable spannable = spannableFactory.newSpannable(text);
	    addSmiles(context, spannable);
	    return spannable;
	}
	
	public static boolean containsKey(String key){
		boolean b = false;
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(key);
	        if (matcher.find()) {
	        	b = true;
	        	break;
	        }
		}
		
		return b;
	}
	
	
	
}
