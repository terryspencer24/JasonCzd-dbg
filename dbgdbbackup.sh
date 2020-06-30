find /root/bkup -type f -name '*.sql' -mtime +30 -exec rm {} \;
docker exec co_dbg-db_1 sh -c 'exec mysqldump --all-databases -uroot -p"pass" ' > "/root/bkup/$(date +"%Y%m%dT%H%M")-dbg.sql"
