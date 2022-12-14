<template>

    <v-card outlined>
        <v-card-title>
            SearchCalendar
        </v-card-title>

        <v-card-text>
            <Date label="From" v-model="value.parameters.from" :editMode="editMode"/>
            <Date label="To" v-model="value.parameters.to" :editMode="editMode"/>
            <String label="UserId" v-model="value.parameters.userId" :editMode="editMode"/>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="search"
            >
                Search
            </v-btn>
        </v-card-actions>
    </v-card>

</template>

<script>
   
    export default {
        name: 'SearchCalendarQuery',
        components:{},
        props: {},
        data: () => ({
            editMode: true,
            value: {
                apiPath: '',
                parameters: {}
            },
        }),
        created() {
            this.value.parameters.from = (new Date(Date.now() - (new Date()).getTimezoneOffset() * 60000)).toISOString().substr(0, 10);
            this.value.parameters.to = (new Date(Date.now() - (new Date()).getTimezoneOffset() * 60000)).toISOString().substr(0, 10);
            this.value.parameters.userId = '';
        },
        watch: {
        },
        methods: {
            search() {
                this.$emit('search', this.value);
            },
            close() {
                this.$emit('closeDialog');
            },
            change() {
                this.$emit('input', this.value);
            },
        }
    }
</script>

