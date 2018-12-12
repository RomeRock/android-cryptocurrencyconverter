package com.romerock.apps.utilities.cryptocurrencyconverter.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.romerock.apps.utilities.cryptocurrencyconverter.DetailsActivity;
import com.romerock.apps.utilities.cryptocurrencyconverter.MainActivity;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.AmountEditTextWidget;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.DialogsHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ItemClickLibraryInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ItemTouchHelperInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.OnStartDragListenerInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.ItemLibraryCurrencyModel;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static android.view.View.VISIBLE;

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
    public RecyclerViewCurrenciesDashboardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_items_currencies_dashboard, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.nameCurrencyHolder = Items.get(position).getName();
        themeSelected = sharedPrefs.getString(context.getString(R.string.preferences_theme_tittle), null);
        if (themeSelected.contains("Night")) {
            viewHolder.txtCurrentItem.setTextColor(context.getResources().getColor(R.color.secundaryTextColorTheme1));
            viewHolder.imgDragNDrop.setColorFilter(context.getResources().getColor(R.color.primaryTextTheme1));
            viewHolder.txtAmountEdit.setTextColor(context.getResources().getColor(R.color.primaryTextTheme1));
        }
        viewHolder.txtCurrentItem.setText(Items.get(position).getCurrency_name());
        viewHolder.txtFlagRowLittle.setText(Items.get(position).getName() + " - " + Items.get(position).getCurrency_symbol());
        viewHolder.txtFlagRow.setText(Items.get(position).getName() + " - " + Items.get(position).getCurrency_symbol());

        String idSearch = Items.get(position).getName().toLowerCase().replace("*","");
        if (idSearch.compareTo("try") == 0)
            idSearch = idSearch + idSearch;
        int id = context.getResources().getIdentifier(idSearch, "drawable", context.getPackageName());
        if (id == 0) {
            id = context.getResources().getIdentifier("generic", "drawable", context.getPackageName());
        }
        viewHolder.imgFlagRow.setImageResource(id);
        viewHolder.imgFlagRowLittle.setImageResource(id);
        viewHolder.imgDragNDrop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (((MainActivity) context).isEditActive) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(viewHolder);
                    }
                    return false;
                }
                return true;
            }
        });
        viewHolder.txtAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //viewHolder.txtAmountEdit.setHint(viewHolder.txtAmount.getText());
                for (int i = 0; i < Items.size(); i++) {
                    if (Items.get(i).getName().compareTo(viewHolder.nameCurrencyHolder) == 0) {
                        ((MainActivity) context).basePosition = i;
                        ((MainActivity) context).baseNameCurrency = Items.get(i).getName();
                    }
                }
                hideAllControllsItems();
                viewHolder.txtAmountEdit.setText("");
                resetMarginRows();
                view.setVisibility(View.GONE);
                viewHolder.txtAmountEdit.setVisibility(VISIBLE);
                viewHolder.relativeLayoutGraph.setVisibility(VISIBLE);
                viewHolder.relativeLayoutGraph.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, DetailsActivity.class);
                        i.putExtra("currencySelected", viewHolder.nameCurrencyHolder);
                        i.putExtra("Currencies", (Serializable) Items);
                        context.startActivity(i);
                    }
                });
                viewHolder.relativeLayout.setVisibility(View.INVISIBLE);
                viewHolder.linCurrencyLittle.setVisibility(View.VISIBLE);
                setMargins((View) viewHolder.txtAmountEdit.getParent(), 0, 16, 0, 16);
                viewHolder.txtAmountEdit.setElevation(5);
                viewHolder.txtAmountEdit.requestFocus();
                Utilities.openKeyboard(context);
                themeSelected = sharedPrefs.getString(context.getString(R.string.preferences_theme_tittle), null);
                if (themeSelected.contains("Night")) {
                    viewHolder.txtCurrentItem.setTextColor(context.getResources().getColor(R.color.colorAccent));
                } else if (themeSelected.contains("Daylight")) {
                    viewHolder.txtCurrentItem.setTextColor(context.getResources().getColor(R.color.colorAccent_Daylight));
                }


            }
        });

        viewHolder.txtAmountEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && viewHolder.imgDelete != null) {
                    resetMarginRows();
                    if (viewHolder.txtAmountEdit.getText().toString().compareTo("") == 0)
                        viewHolder.txtAmountEdit.setText("1.00");
                    viewHolder.txtAmount.setText(Utilities.getNumberString(Double.parseDouble(String.valueOf(((MainActivity) context).baseValue))));
                    if (themeSelected.contains("Night")) {
                        viewHolder.txtCurrentItem.setTextColor(context.getResources().getColor(R.color.secundaryTextColorTheme1));
                    } else {
                        if (themeSelected.contains("Daylight")) {
                            viewHolder.txtCurrentItem.setTextColor(context.getResources().getColor(R.color.secundaryTextColorTheme2));
                        }
                    }

                }
            }
        });
        viewHolder.txtAmountEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().compareTo("") != 0) {
                    // TODO tambien aqui realizar la evaluacion de la expresion
                    if (charSequence.toString().compareTo("") != 0) {
                        //     if (TextUtils.isDigitsOnly(charSequence.toString().substring(charSequence.length()-1))) {
                        double resultexpression = Utilities.eval(charSequence.toString(), context, ((MainActivity) context).getCoordinator());
                        if (!Double.isInfinite(resultexpression) && !Double.isNaN(resultexpression)) {
                            correctExpression = true;
                            ((MainActivity) context).baseValue = resultexpression;
                            makeOperation();
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
        viewHolder.txtAmountEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                        || actionId == EditorInfo.IME_ACTION_SEND) {
                    finishChanges(viewHolder);
                }
                return handled;
            }
        });
        viewHolder.txtAmountEdit.setKeyImeChangeListener(new AmountEditTextWidget.KeyImeChange(){
            @Override
            public void onKeyIme(int keyCode, KeyEvent event)
            {
                if (KeyEvent.KEYCODE_BACK == event.getKeyCode()){
                    hideAllControllsItems();
                    resetMarginRows();
                }
            }});
        // Hide controls and show necesary first time
        viewHolder.imgDelete.setVisibility(View.GONE);
        viewHolder.imgDragNDrop.setVisibility(View.GONE);
        viewHolder.txtAmount.setVisibility(View.VISIBLE);
        viewHolder.txtAmountEdit.setVisibility(View.GONE);
        viewHolder.linCurrencyLittle.setVisibility(View.GONE);
        viewHolder.relativeLayoutGraph.setVisibility(View.GONE);
        viewHolder.relativeLayout.setVisibility(VISIBLE);
        // *************************
        viewHolder.imgDelete.setContentDescription(Items.get(position).getName());
        viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.countTotalKeys(context);
                Utilities.addIntestitial(context, "");
                removeItem(((ImageView) view).getContentDescription().toString());
            }
        });
        if (position != ((MainActivity) context).basePosition) {
            if (((MainActivity) context).basePosition < Items.size()) {
                if(Items.get(position).getName()!=null&&Items.get(position).getCurrency()!=null) {
                    String result = Utilities.makeOperationWithFormat(((MainActivity) context).baseValue, Items.get(position).getCurrency(), Items.get(((MainActivity) context).basePosition).getCurrency());
                    viewHolder.txtAmount.setText(result + "");
                }else{
                    viewHolder.txtAmount.setText("");
                }
            }
        } else
            viewHolder.txtAmount.setText(Utilities.getNumberString(Double.parseDouble(String.valueOf(((MainActivity) context).baseValue))));
        setTheme(viewHolder);
    }



    private void setTheme(ViewHolder viewHolder) {
        String themeSelected = sharedPrefs.getString(context.getString(R.string.preferences_theme_tittle), null);
        if (themeSelected.contains("Night")) {
            viewHolder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.backgroundAccentTheme1));
            viewHolder.relativeLayoutGraph.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            viewHolder.txtFlagRowGraph.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
    }

    private void finishChanges(ViewHolder viewHolder) {
        if (viewHolder.txtAmountEdit.getText().toString().compareTo("") == 0)
            viewHolder.txtAmountEdit.setText("1.00");
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(viewHolder.txtAmountEdit.getWindowToken(), 0);
        if (correctExpression) {
            viewHolder.txtAmount.setText(Utilities.getNumberString(Double.parseDouble(String.valueOf(((MainActivity) context).baseValue))));
            hideAllControllsItems();
            resetMarginRows();

            if (themeSelected.contains("Night")) {
                viewHolder.txtCurrentItem.setTextColor(context.getResources().getColor(R.color.secundaryTextColorTheme1));
            } else {
                if (themeSelected.contains("Daylight")) {
                    viewHolder.txtCurrentItem.setTextColor(context.getResources().getColor(R.color.secundaryTextColorTheme2));
                }
            }
        } else {
            DialogsHelper.showSnackBar(((MainActivity) context).getCoordinator(), context.getString(R.string.error_eval), context.getResources().getColor(R.color.alert_snackbar));
        }
    }


    @Override
    public int getItemCount() {
        if(Items==null)
            return 0;
        else
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
        try {
            Collections.swap(Items, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
            ViewHolder holderHide;
            holderHide = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(0);
            ((MainActivity) context).baseValue = (double) Utilities.getNumberDecimal(holderHide.txtAmount.getText().toString());
            ((MainActivity) context).basePosition = 0;
            return true;
        }catch (Exception e){return false;}
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
                            ((MainActivity) context).baseValue = Double.parseDouble(holderHide.txtAmount.getText().toString());
                        }*/
                        ViewHolder holderChange;
                        holderChange = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(0);
                        if (holderHide != null)
                            ((MainActivity) context).baseValue = Double.parseDouble(holderChange.txtAmount.getText().toString().replace(",",""));
                    } else {
                        ((MainActivity) context).getTxtEditText().setVisibility(View.GONE);
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

    public void showAllControllsItems() {
        ViewHolder holderHide;
        for (int pos = 0; pos < Items.size(); pos++) {
            holderHide = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(pos);
            if (holderHide != null) {
                holderHide.imgDelete.setVisibility(VISIBLE);
                holderHide.imgDragNDrop.setVisibility(VISIBLE);
                holderHide.txtAmount.setVisibility(View.GONE);
            }
        }
    }

    public void hideAllControllsItems() {
        Utilities.closeKeyboard(context);
        ViewHolder holderHide;
        for (int pos = 0; pos < Items.size(); pos++) {
            holderHide = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(pos);
            if (holderHide != null) {
                holderHide.imgDelete.setVisibility(View.GONE);
                holderHide.imgDragNDrop.setVisibility(View.GONE);
                holderHide.txtAmount.setVisibility(View.VISIBLE);
                holderHide.txtAmountEdit.setVisibility(View.GONE);
                holderHide.linCurrencyLittle.setVisibility(View.GONE);
                holderHide.relativeLayoutGraph.setVisibility(View.GONE);
                holderHide.relativeLayout.setVisibility(VISIBLE);
            }
        }
    }

    public void makeOperation() {
        if (correctExpression) {
            ViewHolder holderHide;
            for (int pos = 0; pos < Items.size(); pos++) {
                holderHide = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(pos);
                if (holderHide != null) {
                    if (((MainActivity) context).basePosition != pos&&Items.get(pos).getCurrency()!=null) {
                        String result = Utilities.makeOperationWithFormat(((MainActivity) context).baseValue, Items.get(pos).getCurrency(), Items.get(((MainActivity) context).basePosition).getCurrency());
                        holderHide.txtAmount.setText(result + "");
                    } else
                        holderHide.txtAmount.setText(((MainActivity) context).baseValue + "");
                }
            }
        } else {
            DialogsHelper.showSnackBar(((MainActivity) context).getCoordinator(), context.getString(R.string.error_eval), context.getResources().getColor(R.color.alert_snackbar));
        }
    }

    public void resetMarginRows() {
        ViewHolder holderHide;
        for (int pos = 0; pos < Items.size(); pos++) {
            holderHide = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(pos);
            if (holderHide != null)
                setMargins((View) holderHide.txtAmount.getParent(), 0, 0, 0, 0);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgDelete;
        public TextView txtCurrentItem;
        public ImageView imgFlagRow;
        public ImageView imgFlagRowLittle;
        public LinearLayout linCurrencyLittle;
        public TextView txtAmount;
        public AmountEditTextWidget txtAmountEdit;
        public TextView txtFlagRow;
        public TextView txtFlagRowLittle;
        public RelativeLayout relRowItemCurrency;
        public ImageView imgDragNDrop;
        public RelativeLayout relativeLayout;
        public RelativeLayout relativeLayoutGraph;
        public String nameCurrencyHolder;
        public TextView txtFlagRowGraph;
        public LinearLayout linClickArea;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtCurrentItem = (TextView) itemLayoutView.findViewById(R.id.txtCurrentItem);
            txtAmount = (TextView) itemLayoutView.findViewById(R.id.txtAmount);
            txtAmountEdit = (AmountEditTextWidget) itemLayoutView.findViewById(R.id.txtAmountEdit);
            txtFlagRow = (TextView) itemLayoutView.findViewById(R.id.txtFlagRow);
            imgFlagRowLittle = (ImageView) itemLayoutView.findViewById(R.id.imgFlagRowLittle);
            imgDelete = (ImageView) itemLayoutView.findViewById(R.id.imgDelete);
            imgFlagRow = (ImageView) itemLayoutView.findViewById(R.id.imgFlagRow);
            linCurrencyLittle = (LinearLayout) itemLayoutView.findViewById(R.id.linCurrencyLittle);
            txtFlagRowLittle = (TextView) itemLayoutView.findViewById(R.id.txtFlagRowLittle);
            relRowItemCurrency = (RelativeLayout) itemLayoutView.findViewById(R.id.relRowItemCurrency);
            imgDragNDrop = (ImageView) itemLayoutView.findViewById(R.id.imgDragNDrop);
            relativeLayout = (RelativeLayout) itemLayoutView.findViewById(R.id.relativeLayout);
            relativeLayoutGraph = (RelativeLayout) itemLayoutView.findViewById(R.id.relativeLayoutGraph);
            txtFlagRowGraph=(TextView) itemLayoutView.findViewById(R.id.txtFlagRowGraph);
        }
    }

}