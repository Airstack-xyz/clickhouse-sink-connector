cd ../python

DATABASE=datatypes

python3 db_compare/mysql_table_checksum.py --mysql_host localhost --mysql_user root --mysql_password root  --mysql_database $DATABASE --tables_regex "^*" --debug_output
python3 db_compare/clickhouse_table_checksum.py --clickhouse_host localhost --clickhouse_user root --clickhouse_password root  --clickhouse_database $DATABASE --tables_regex "^*" --debug_output

## DATE and DATETIME
for tableName in temporal_types_DATE temporal_types_DATETIME temporal_types_DATETIME1 temporal_types_DATETIME2 temporal_types_DATETIME3 temporal_types_DATETIME4 temporal_types_DATETIME5 temporal_types_DATETIME6
do
  echo " DIFF TABLE ****${tableName} **** "
  diff out.${tableName}.mysql.txt out.${tableName}.ch.txt
done

# TIME and TIMESTAMP
for tableName in temporal_types_TIME temporal_types_TIME1 temporal_types_TIME2 temporal_types_TIME3 temporal_types_TIME4 temporal_types_TIME5 temporal_types_TIME6
do
  echo " DIFF TABLE ****${tableName} **** "
  diff out.${tableName}.mysql.txt out.${tableName}.ch.txt
done

# TIMESTAMP
for tableName in temporal_types_TIMESTAMP temporal_types_TIMESTAMP1 temporal_types_TIMESTAMP2 temporal_types_TIMESTAMP3 temporal_types_TIMESTAMP4 temporal_types_TIMESTAMP5 temporal_types_TIMESTAMP6
do
  echo " DIFF TABLE ****${tableName} **** "
  diff out.${tableName}.mysql.txt out.${tableName}.ch.txt
done

#diff ../python/

#rm -fr *.ch.txt
#rm -fr *.mysql.txt