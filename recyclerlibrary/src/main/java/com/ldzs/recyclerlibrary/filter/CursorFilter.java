/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Duplicate of the android.Widget.CursorFilter in order to make it public
 */

package com.ldzs.recyclerlibrary.filter;

import android.database.Cursor;
import android.widget.Filter;

/*
 * Duplicate of the android.Widget.CursorFilter in order to make it public
 */
public class CursorFilter extends Filter {
    CursorFilterClient filterClient;

    public CursorFilter(CursorFilterClient client) {
        this.filterClient = client;
    }

    @Override
    public CharSequence convertResultToString(Object resultValue) {
        return filterClient.convertToString((Cursor) resultValue);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        Cursor cursor = filterClient.runQueryOnBackgroundThread(constraint);

        FilterResults results = new FilterResults();
        if (cursor != null) {
            results.count = cursor.getCount();
            results.values = cursor;
        } else {
            results.count = 0;
            results.values = null;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        Cursor oldCursor = filterClient.getCursor();

        if (results.values != null && results.values != oldCursor) {
            filterClient.changeCursor((Cursor) results.values);
        }
    }

    public interface CursorFilterClient {
        CharSequence convertToString(Cursor cursor);

        Cursor runQueryOnBackgroundThread(CharSequence constraint);

        Cursor getCursor();

        void changeCursor(Cursor cursor);
    }
}