# Common functions

function send() #(url, method, payload)
{
    curl -H 'Content-Type: application/json' \
         -H 'Accept: application/json' \
         -d"$3" -s \
         $1/$2
}

function updateConfig() #(url, config)
{
    curl -H 'Content-Type: application/json' \
         -H 'Accept: application/json' \
         -d"$2" -s \
         $1/v1/admin/config
}
