<template>

    <v-card outlined>
        <v-card-title>
            Use
        </v-card-title>

        <v-card-text>
            <Number label="DayCount" v-model="value.dayCount" :editMode="editMode"/>
            <String label="Reason" v-model="value.reason" :editMode="editMode"/>
            <String label="VacationId" v-model="value.vacationId" :editMode="editMode"/>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="use"
            >
                Use
            </v-btn>
            
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="close"
            >
                Close
            </v-btn>
        </v-card-actions>
    </v-card>

</template>

<script>
   
    export default {
        name: 'UseCommand',
        components:{},
        props: {},
        data: () => ({
            editMode: true,
            value: {},
        }),
        created() {
            this.value.dayCount = 0;
            this.value.reason = '';
            this.value.vacationId = '';
        },
        watch: {
        },
        methods: {
            use() {
                this.$emit('use', this.value);
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

