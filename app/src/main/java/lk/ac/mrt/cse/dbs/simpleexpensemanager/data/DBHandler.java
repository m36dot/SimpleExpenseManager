package lk.ac.mrt.cse.dbs.simpleexpensemanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.R;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "150724U";
    // Accounts table name
    private static final String TABLE_ACCOUNT = "accounts";
    // Transactions table name
    private static final String TABLE_TRANSACTION = "transactions";


    // Accounts Table Columns names
    private static final String KEY_ACCOUNT_NO = "accountNo";
    private static final String KEY_BANKNAME = "bankName";
    private static final String KEY_ACCOUNT_HOLDER = "accountHolderName";
    private static final String KEY_BALANCE = "balance";


    /*
    * private Date date;
    private String accountNo;
    private ExpenseType expenseType;
    private double amount;*/
    // Accounts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DATE="date";
    private static final String KEY_ACCOUNTNO = "accountNo";
    private static final String KEY_EXPENSE = "expenseType";
    private static final String KEY_AMOUNT = "amount";

private Context context;

    public DBHandler(Context context) {


        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ACCOUNTS_TABLE = "CREATE TABLE " + TABLE_ACCOUNT + "("
        + KEY_ACCOUNT_NO + " PRIMARY KEY," + KEY_BANKNAME + " TEXT,"
        + KEY_ACCOUNT_HOLDER + " TEXT," + KEY_BALANCE + " DOUBLE" + ")";

        String CREATE_TRANSACTION_TABLE = "CREATE TABLE " + TABLE_TRANSACTION + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE + " TEXT,"
                + KEY_ACCOUNTNO + " TEXT," + KEY_EXPENSE + " TEXT," + KEY_AMOUNT + " DOUBLE" + ")";

        Log.e("successcalled","over");
        db.execSQL(CREATE_ACCOUNTS_TABLE);
        db.execSQL(CREATE_TRANSACTION_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_TRANSACTION);

// Creating tables again
        onCreate(db);
    }
    public void addAccount(Account acct) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ACCOUNT_NO, acct.getAccountNo());
        values.put(KEY_BANKNAME, acct.getBankName());
        values.put(KEY_ACCOUNT_HOLDER, acct.getAccountHolderName());
        values.put(KEY_BALANCE, acct.getBalance());
        Log.e("GOTSOFAR","OVER");
        // Inserting Row
       db.insert(TABLE_ACCOUNT, null, values);

        db.close(); // Closing database connection
    }
    public void addTransaction(Transaction trans) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATE,trans.getDate().toString() );
        values.put(KEY_ACCOUNTNO, trans.getAccountNo());
        values.put(KEY_EXPENSE, trans.getExpenseType().toString());
        values.put(KEY_AMOUNT, trans.getAmount());

        // Inserting Row
        db.insert(TABLE_TRANSACTION, null, values);
        db.close(); // Closing database connection
    }
    public Map<String, Account> getAllAccounts() {
        Map<String, Account>  accounts = new HashMap<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_ACCOUNT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if(!cursor.moveToFirst()){
            Log.e("nothinggothere","over");
        }
        if (cursor.moveToFirst()) {
            Log.e("step1","over");
            do {
                Account acct = new Account();
                acct.setAccountNo(cursor.getString(0));
                acct.setBankName(cursor.getString(1));
                acct.setAccountHolderName(cursor.getString(2));
                acct.setBalance(cursor.getDouble(3));

                // Adding contact to list
                accounts.put(acct.getAccountNo(), acct);
            } while (cursor.moveToNext());
        }
        // return contact list

        return accounts;
    }
    public List<Transaction> getAllTransactions() {
        List<Transaction>  transs = new ArrayList<Transaction>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_TRANSACTION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Transaction trans = new Transaction();

                Calendar cal = Calendar.getInstance();
                Log.e("DATEWEGET",cursor.getString(1)+"");
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                Date date=null;
                try {
                    cal.setTime(sdf.parse(cursor.getString(1)));
                    date=cal.getTime();
                    Log.e("DATEWEPROCESS",""+date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                trans.setDate(date);
                trans.setAccountNo(cursor.getString(2));
                trans.setExpenseType(ExpenseType.valueOf(cursor.getString(3)));
                trans.setAmount(cursor.getDouble(4));

                // Adding contact to list
                transs.add(trans);
            } while (cursor.moveToNext());
        }
        // return contact list

        return transs;
    }
    // Deleting a Account
    public void deleteAccount(String acct) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCOUNT, KEY_ACCOUNT_NO + " = ?",
                new String[] { acct });
        db.close();
    }

    public int updateAccount(Account acct) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ACCOUNT_NO, acct.getAccountNo());
        values.put(KEY_BANKNAME, acct.getBankName());
        values.put(KEY_ACCOUNT_HOLDER, acct.getAccountHolderName());
        values.put(KEY_BALANCE, acct.getBalance());
        // updating row
        int i= db.update(TABLE_ACCOUNT, values, KEY_ACCOUNT_NO + " = ?",
                new String[]{acct.getAccountNo()});
        db.close();
        return i;
    }
}