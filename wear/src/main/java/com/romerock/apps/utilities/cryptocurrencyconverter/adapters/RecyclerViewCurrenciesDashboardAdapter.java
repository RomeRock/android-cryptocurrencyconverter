package com.romerock.apps.utilities.cryptocurrencyconverter.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.romerock.apps.utilities.cryptocurrencyconverter.MainActivity;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities_wear;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.SimpleItemTouchHelperCallback;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ItemClickLibraryInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ItemTouchHelperInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.OnStartDragListenerInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.ItemLibraryCurrencyModel;

import java.util.Collections;
import java.util.List;

/**
 * Created by Ebricko on 15/12/2016.
 */
public class RecyclerViewCurrenciesDashboardAdapter extends RecyclerView.Adapter<RecyclerViewCurrenciesDashboardAdapter.ViewHolder> implements View.OnClickListener, ItemTouchHelperInterface {
    private final ItemClickLibraryInterface itemClickListener;
    private final OnStartDragListenerInterface mDragStartListener;
    private List<ItemLibraryCurrencyModel> Items;
    private int position;
    private Context context;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor ed;
    private boolean correctExpression;
    private String themeSelected;

    public RecyclerViewCurrenciesDashboardAdapter(List<ItemLibraryCurrencyModel> Items, Context context, @NonNull ItemClickLibraryInterface listener, OnStartDragListenerInterface dragStartListener, RecyclerView recyclerView) {
        this.Items = Items;
        this.itemClickListener = listener;
        this.context = context;
        this.mDragStartListener = dragStartListener;
        this.recyclerView = recyclerView;
        sharedPrefs = context.getSharedPreferences(context.getString(R.string.preferences_name), context.MODE_PRIVATE);
        ed = sharedPrefs.edit();
    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public List<ItemLibraryCurrencyModel> getItems() {
        return Items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_currency, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.txtCurrent.setText(Items.get(position).getCurrency_name());
        viewHolder.nameCurrencyHolder=Items.get(position).getName();
        int id = context.getResources().getIdentifier(Items.get(position).getName().toLowerCase().replace("*",""), "drawable", context.getPackageName());
        if (id == 0) {
            id = context.getResources().getIdentifier("generic", "drawable", context.getPackageName());
        }
        viewHolder.imgFlag.setImageResource(id);
        viewHolder.txtCurrentAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

      /*  viewHolder.imgDragNDrop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(viewHolder);
                    }
                return true;
            }
        });*/

        viewHolder.txtCurrentAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && viewHolder.imgFlag != null) {
                    if (viewHolder.txtCurrentAmount.getText().toString().compareTo("") == 0)
                        viewHolder.txtCurrentAmount.setText("1.00");
                   // viewHolder.txtCurrentAmount.setText(Utilities_wear.formatAmountOperation(String.valueOf(((MainActivity) context).baseValue)));


                }
            }
        });
        viewHolder.txtCurrentAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                        || actionId == EditorInfo.IME_ACTION_SEND) {
                    finishChanges(viewHolder);
                    makeOperation();
                }
                return handled;
            }
        });

        viewHolder.txtCurrentAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().compareTo("") != 0) {
                    // TODO tambien aqui realizar la evaluacion de la expresion
                    if (charSequence.toString().compareTo("") != 0) {
                        //     if (TextUtils.isDigitsOnly(charSequence.toString().substring(charSequence.length()-1))) {
                        double resultexpression = Utilities_wear.eval(charSequence.toString(), context);
                        if (!Double.isInfinite(resultexpression) && !Double.isNaN(resultexpression)) {
                            correctExpression = true;
                            ((MainActivity) context).baseValue = resultexpression;
                        }
                    } else {
                        correctExpression = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        viewHolder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < Items.size(); i++) {
                    if (Items.get(i).getName().compareTo(viewHolder.nameCurrencyHolder) == 0) {
                        ((MainActivity) context).basePosition = i;
                        ((MainActivity) context).baseNameCurrency = Items.get(i).getName();
                    }
                }
                viewHolder.row.setBackgroundColor(context.getResources().getColor(R.color.colorAccent_Daylight));
                viewHolder.txtCurrent.setTextColor(context.getResources().getColor(R.color.colorAccent));
                viewHolder.txtCurrentAmount.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                viewHolder.txtCurrentAmount.setText("");
                viewHolder.txtCurrentAmount.requestFocus();
                Utilities_wear.openKeyboard(context);
            }
        });
        viewHolder.row.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_MOVE) {
                        mDragStartListener.onStartDrag(viewHolder);
                        int y = SimpleItemTouchHelperCallback.y;
                        Log.d("YYYYY","es y: "+y);
                        if(y>20) {
                            Items.remove(position);
                            notifyDataSetChanged();
                            String defaultCurrencies = "";
                            sharedPrefs = context.getSharedPreferences(context.getString(R.string.preferences_name), context.MODE_PRIVATE);
                            ed = sharedPrefs.edit();
                            for (ItemLibraryCurrencyModel item : Items) {
                                defaultCurrencies += item.getName() + "|";
                            }
                            ed.putString(context.getString(R.string.preferences_defaultCurrencies), defaultCurrencies);
                            ed.commit();
                        }
                        }
                return true;
            }
        });

        // *************************

       /* viewHolder.imgEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(((ImageView) view).getContentDescription().toString());
            }
        });*/
    }

    public void hideAllControllsItems() {
        ViewHolder holderHide;
        for (int pos = 0; pos < Items.size(); pos++) {
            holderHide = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(pos);
            if (holderHide != null) {
                holderHide.row.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holderHide.txtCurrent.setTextColor(context.getResources().getColor(R.color.secundaryTextColorTheme1));
                holderHide. txtCurrentAmount.setTextColor(context.getResources().getColor(R.color.white));

            }
        }
    }

    


    @Override
    public int getItemCount() {
        return Items.size();
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onItemDismiss(int position) {
        Items.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(Items, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        ViewHolder holderHide;
        holderHide = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(0);
        ((MainActivity) context).basePosition = 0;
        return true;
    }

    public void removeItem(String nameDelete) {
        int positionDelete = 0;
        for (int i = 0; i < Items.size(); i++) {
            if (Items.get(i).getName().equals(nameDelete)) {
                Items.remove(i);
                positionDelete = i;
                notifyItemRemoved(positionDelete);
                if (positionDelete == ((MainActivity) context).basePosition) {
                    ((MainActivity) context).basePosition = 0;
                    if (Items.size() > 0) {
                        ViewHolder holderHide;
                        holderHide = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(0);
                       /* if (holderHide != null) {
                            ((MainActivity) context).baseValue = Double.parseDouble(holderHide.txtCurrentAmount.getText().toString());
                        }*/
                        ViewHolder holderChange;
                        holderChange = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(0);
                        if (holderHide != null)
                            ((MainActivity) context).baseValue = Double.parseDouble(holderChange.txtCurrentAmount.getText().toString());
                    } else {
                        ((MainActivity) context).baseValue = 1;
                        ((MainActivity) context).basePosition = 0;
                    }
                }
            }
        }

        String defaultCurrencies = "";
        for (ItemLibraryCurrencyModel item : Items) {
            defaultCurrencies += item.getName() + "|";
        }
        ed.putString(context.getString(R.string.preferences_defaultCurrencies), defaultCurrencies);
        ed.commit();
    }



    public void makeOperation() {
            ViewHolder holderHide;
            for (int pos = 0; pos < Items.size(); pos++) {
                holderHide = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(pos);
                if (holderHide != null) {
                    if (((MainActivity) context).basePosition != pos) {
                        String result="";// = Utilities_wear.makeOperationWithFormat(((MainActivity) context).baseValue, Items.get(pos).getCurrency(), Items.get(((MainActivity) context).basePosition).getCurrency());
                        holderHide.txtCurrentAmount.setText(result + "");
                    } else
                        holderHide.txtCurrentAmount.setText(((MainActivity) context).baseValue + "");
                }
            }
    }

    private void finishChanges(ViewHolder viewHolder) {
        if (viewHolder.txtCurrentAmount.getText().toString().compareTo("") == 0)
            viewHolder.txtCurrentAmount.setText("1.00");
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(viewHolder.txtCurrentAmount.getWindowToken(), 0);
        if (correctExpression) {
            //viewHolder.txtCurrentAmount.setText(Utilities_wear.formatAmountOperation(String.valueOf(((MainActivity) context).baseValue)));
            hideAllControllsItems();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgFlag;
        public TextView txtCurrent;
        public EditText txtCurrentAmount;
        public RelativeLayout row;
        public String nameCurrencyHolder;
        public ImageView imgDragNDrop;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtCurrent = (TextView) itemLayoutView.findViewById(R.id.txtCurrent);
            txtCurrentAmount =(EditText)itemLayoutView.findViewById(R.id.txtCurrentAmount);
            imgFlag =(ImageView) itemLayoutView.findViewById(R.id.imgFlagRow);
            row=(RelativeLayout)itemLayoutView.findViewById(R.id.relRow);
            imgDragNDrop=(ImageView)itemLayoutView.findViewById(R.id.imgDragNDrop);
        }
    }



}