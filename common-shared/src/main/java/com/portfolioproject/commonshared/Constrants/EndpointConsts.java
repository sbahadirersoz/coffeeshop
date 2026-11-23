package com.portfolioproject.commonshared.Constrants;

public class EndpointConsts
{
    public  static final String Root = "/api/v1";



    public static class AdminPanelEndpointConstrants

    {
        public static   final String Base =  Root+"/AdminPanel";

        public static final String GetAll =  Base;
        public static final String FindById =  Base+"/{id}";

    }
}
