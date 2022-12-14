<template>

    <v-card style="width:450px;" outlined>
        <template slot="progress">
            <v-progress-linear
                    color="deep-purple"
                    height="10"
                    indeterminate
            ></v-progress-linear>
        </template>

        <v-card-title v-if="value._links">
            Vacation # {{value._links.self.href.split("/")[value._links.self.href.split("/").length - 1]}}
        </v-card-title >
        <v-card-title v-else>
            Vacation
        </v-card-title >

        <v-card-text>
            <Date label="StartDate" v-model="value.startDate" :editMode="editMode"/>
            <Date label="EndDate" v-model="value.endDate" :editMode="editMode"/>
            <String label="Reason" v-model="value.reason" :editMode="editMode"/>
            <String label="UserId" v-model="value.userId" :editMode="editMode"/>
            <Number label="Days" v-model="value.days" :editMode="editMode"/>
            <String label="Status" v-model="value.status" :editMode="editMode"/>

            <EventViewer
                v-if="value._links && value._links.events"
                :src="value._links.events.href"
            >
            </EventViewer>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="edit"
                    v-if="!editMode"
            >
                Edit
            </v-btn>
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="save"
                    v-else
            >
                RegisterVacation
            </v-btn>
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="remove"
                    v-if="!editMode"
            >
                Delete
            </v-btn>
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="editMode = false"
                    v-if="editMode && !isNew"
            >
                Cancel
            </v-btn>
        </v-card-actions>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                    v-if="!editMode"
                    color="deep-purple lighten-2"
                    text
                    @click="cancel"
            >
                Cancel
            </v-btn>
            <v-btn
                    v-if="!editMode"
                    color="deep-purple lighten-2"
                    text
                    @click="openApprove"
            >
                Approve
            </v-btn>
            <v-dialog v-model="approveDiagram" width="500">
                <ApproveCommand
                        @closeDialog="closeApprove"
                        @approve="approve"
                ></ApproveCommand>
            </v-dialog>
            <v-btn
                    v-if="!editMode"
                    color="deep-purple lighten-2"
                    text
                    @click="confirmUsed"
            >
                ConfirmUsed
            </v-btn>
            <v-btn
                    v-if="!editMode"
                    color="deep-purple lighten-2"
                    text
                    @click="update"
            >
                Update
            </v-btn>
        </v-card-actions>

        <v-snackbar
                v-model="snackbar.status"
                :top="true"
                :timeout="snackbar.timeout"
                color="error"
        >
            {{ snackbar.text }}
            <v-btn dark text @click="snackbar.status = false">
                Close
            </v-btn>
        </v-snackbar>
    </v-card>

</template>

<script>
    const axios = require('axios').default;

    import { RSocketClient } from 'rsocket-core';
    import RSocketWebSocketClient from 'rsocket-websocket-client';
    import { IdentitySerializer, JsonSerializer } from "rsocket-core/build";



    export default {
        name: 'Vacation',
        components:{
        },
        props: {
            value: [Object, String, Number, Boolean, Array],
            editMode: Boolean,
            isNew: Boolean,
            offline: Boolean,
        },
        data: () => ({
            snackbar: {
                status: false,
                timeout: 5000,
                text: ''
            },
            approveDiagram: false,
        }),
        created(){
            if(this.isNew) return;

            var websocketUrl = new URL(window.location.href);

            websocketUrl.protocol = "wss";
            websocketUrl.pathname = "/rsocket/vacations";
            websocketUrl.hash = "";
            
            var me = this;

            const transport = new RSocketWebSocketClient(
                {
                    url: websocketUrl.href
                }
            );
            const client = new RSocketClient({
                // send/receive JSON objects instead of strings/buffers
                serializers: {
                data: JsonSerializer,
                metadata: IdentitySerializer
                },
                setup: {
                // ms btw sending keepalive to server
                keepAlive: 60000,
                // ms timeout if no keep-alive response
                lifetime: 180000,
                dataMimeType: "application/json",
                metadataMimeType: 'message/x.rsocket.routing.v0'
                },
                transport
            });
            client.connect().subscribe({
                onComplete: socket => {
                let requestedMsg = 10;

                // console.log("connected to rsocket"); // debug
                const endpoint = "vacations."+ me.value.id +".get"
                socket.requestStream({
                    data: {},
                    metadata: String.fromCharCode(endpoint.length) + endpoint
                })
                    .subscribe({
                        onSubscribe: (sub) => {
                            console.log("subscribed to server stream"); // debug
                            this.requestStreamSubscription = sub
                            this.requestStreamSubscription.request(requestedMsg)
                        },
                        onNext: (e) => {
                            e.data._links = me.value._links;
                            me.value = e.data
                            
                        },
                        onError: error => {
                            // console.log("got error with requestStream"); // debug
                            console.error(error);
                        },
                        onComplete: () => {
                            // console.log("requestStream completed"); // debug
                        }
                    });
                },
                onError: error => {
                    console.error(error);
                },
                // onSubscribe: cancel => {
                // }
            });

        },
        computed:{
        },
        methods: {
            selectFile(){
                if(this.editMode == false) {
                    return false;
                }
                var me = this
                var input = document.createElement("input");
                input.type = "file";
                input.accept = "image/*";
                input.id = "uploadInput";
                
                input.click();
                input.onchange = function (event) {
                    var file = event.target.files[0]
                    var reader = new FileReader();

                    reader.onload = function () {
                        var result = reader.result;
                        me.imageUrl = result;
                        me.value.photo = result;
                        
                    };
                    reader.readAsDataURL( file );
                };
            },
            edit() {
                this.editMode = true;
            },
            async save(){
                try {
                    var temp = null;

                    if(!this.offline) {
                        if(this.isNew) {
                            temp = await axios.post(axios.fixUrl('/vacations'), this.value)
                        } else {
                            temp = await axios.put(axios.fixUrl(this.value._links.self.href), this.value)
                        }
                    }

                    if(this.value!=null) {
                        for(var k in temp.data) this.value[k]=temp.data[k];
                    } else {
                        this.value = temp.data;
                    }

                    this.editMode = false;
                    this.$emit('input', this.value);

                    if (this.isNew) {
                        this.$emit('add', this.value);
                    } else {
                        this.$emit('edit', this.value);
                    }

                    location.reload()

                } catch(e) {
                    this.snackbar.status = true
                    if(e.response && e.response.data.message) {
                        this.snackbar.text = e.response.data.message
                    } else {
                        this.snackbar.text = e
                    }
                }
                
            },
            async remove(){
                try {
                    if (!this.offline) {
                        await axios.delete(axios.fixUrl(this.value._links.self.href))
                    }

                    this.editMode = false;
                    this.isDeleted = true;

                    this.$emit('input', this.value);
                    this.$emit('delete', this.value);

                } catch(e) {
                    this.snackbar.status = true
                    if(e.response && e.response.data.message) {
                        this.snackbar.text = e.response.data.message
                    } else {
                        this.snackbar.text = e
                    }
                }
            },
            change(){
                this.$emit('input', this.value);
            },
            async cancel() {
                try {
                    if(!this.offline) {
                        var temp = await axios.put(axios.fixUrl(this.value._links['cancel'].href))
                        for(var k in temp.data) {
                            this.value[k]=temp.data[k];
                        }
                    }

                    this.editMode = false;
                } catch(e) {
                    this.snackbar.status = true
                    if(e.response && e.response.data.message) {
                        this.snackbar.text = e.response.data.message
                    } else {
                        this.snackbar.text = e
                    }
                }
            },
            async approve(params) {
                try {
                    if(!this.offline) {
                        var temp = await axios.put(axios.fixUrl(this.value._links['approve'].href), params)
                        for(var k in temp.data) {
                            this.value[k]=temp.data[k];
                        }
                    }

                    this.editMode = false;
                    this.closeApprove();
                } catch(e) {
                    this.snackbar.status = true
                    if(e.response && e.response.data.message) {
                        this.snackbar.text = e.response.data.message
                    } else {
                        this.snackbar.text = e
                    }
                }
            },
            openApprove() {
                this.approveDiagram = true;
            },
            closeApprove() {
                this.approveDiagram = false;
            },
            async confirmUsed() {
                try {
                    if(!this.offline) {
                        var temp = await axios.put(axios.fixUrl(this.value._links['confirmused'].href))
                        for(var k in temp.data) {
                            this.value[k]=temp.data[k];
                        }
                    }

                    this.editMode = false;
                } catch(e) {
                    this.snackbar.status = true
                    if(e.response && e.response.data.message) {
                        this.snackbar.text = e.response.data.message
                    } else {
                        this.snackbar.text = e
                    }
                }
            },
            async update() {
                try {
                    if(!this.offline) {
                        var temp = await axios.put(axios.fixUrl(this.value._links['update'].href))
                        for(var k in temp.data) {
                            this.value[k]=temp.data[k];
                        }
                    }

                    this.editMode = false;
                } catch(e) {
                    this.snackbar.status = true
                    if(e.response && e.response.data.message) {
                        this.snackbar.text = e.response.data.message
                    } else {
                        this.snackbar.text = e
                    }
                }
            },


        },
    }
</script>

