#!/bin/bash
export VERSION=$1
export BASE_URL=$2
export BASE_PATH=$3
export USERNAME=$4
export PASSWORD=$5
export NUM_OF_PROC=$6

export BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
export REPOSITORY_DIR="$BASE_DIR/$BASE_PATH"

callUpload() {
  curl --write-out ' %{url_effective} Bytes: %{size_upload} Response code: %{http_code}\n' --insecure --silent --output /dev/null --user "$USERNAME:$PASSWORD" --upload-file $REPOSITORY_DIR$1 $BASE_URL/$VERSION$1
}
export -f callUpload

REP_DIR_LEN=`echo -n "$REPOSITORY_DIR" | wc -c`; REP_DIR_LEN=$(($REP_DIR_LEN + 1))

find $REPOSITORY_DIR -type f -print0 | xargs -0 -I file echo "file" | cut -c$REP_DIR_LEN- | xargs -P $NUM_OF_PROC -I {} bash -c 'callUpload "$@"' _ {} 

