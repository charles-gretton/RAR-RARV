


#include <iostream>
#include <string>
#include <fstream>
#include <sstream>
#include <vector>
#include <map>
#include <set>
#include <algorithm>
#include <cassert>
#include <cctype>

using namespace std;

double coordinate_multiplication_factor = 1.0;

string problem_name = "";
int global__customer_value; 
string global__input_filename; 
map<string, int> vehicleid_to__capacity;
map<string, int> vehicleid_to__duration;

int location_counter = 0;
map<int, pair<double, double> > locationid__to__location;
map<pair<double, double>,  int> location__to__locationid;

map<int, int> customer__to__locationid;

map<int, int> customer__to__demand;
map<int, int> customer__to__start_time_window;
map<int, int> customer__to__end_time_window;
map<int, int> customer__to__duration;
map<int, int> customer__to__value;

void parse(ifstream& in)
{
    bool parsed_problem_name = false;

    bool parsed_VEHICLE = false;

    bool parsed_NUMBER_and_CAPACITY_line = false;
    bool parsed_VEHICLES_COUNT_and_CAPACITIES = false;
    bool parsed_CUSTOMER = false;
    bool parsed__CUST_NO__XCOORD__YCOORD__DEMAND__READY_TIME__DUE_DATE__SERVICE_TIME = false;
    if(in.eof())return;
    string tmp;
    do{
        getline(in, tmp);
        if(!tmp.size())continue;

        if(!parsed_problem_name){
            parsed_problem_name = true;
            istringstream iss(tmp);
            iss>>problem_name;
            cerr<<"Parsing problem name("<<problem_name<<").\n";
        } else if (!parsed_VEHICLE){
            cerr<<"Parsing vehicle table acknowledgment.\n";
            parsed_VEHICLE = true;
            istringstream iss(tmp);
            string str;
            iss>>str;
            assert(str == "VEHICLE");
            if(str != "VEHICLE"){cerr<<"Input is badly formatted!\n";exit(-1);}
            
        } else if (!parsed_NUMBER_and_CAPACITY_line){
            cerr<<"Parsing vehicle table headings.\n";
            parsed_NUMBER_and_CAPACITY_line = true;
            istringstream iss(tmp);
            string str;
            iss>>str;
            assert(str == "NUMBER");
            if(str != "NUMBER"){cerr<<"Input is badly formatted!\n";exit(-1);}
            iss>>str;
            assert(str == "CAPACITY");
            if(str != "CAPACITY"){cerr<<"Input is badly formatted!\n";exit(-1);}
                
        } else if (!parsed_VEHICLES_COUNT_and_CAPACITIES){
            cerr<<"Parsing vehicle table.\n";
            parsed_VEHICLES_COUNT_and_CAPACITIES= true;
            istringstream iss(tmp);
            int number_of_vehicles;
            iss>>number_of_vehicles;
            int capacity_of_vehicles;
            iss>>capacity_of_vehicles;

            for(size_t i = 1; i <=  number_of_vehicles; i++){
                ostringstream oss;
                oss<<"veh_"<<i;
                vehicleid_to__capacity[oss.str()] = capacity_of_vehicles;
            }
        } else if (!parsed_CUSTOMER){
            cerr<<"Parsing customer table acknowledgment.\n";
            parsed_CUSTOMER = true;
            istringstream iss(tmp);
            string str;
            iss>>str;
            assert(str == "CUSTOMER");
            if(str != "CUSTOMER"){cerr<<"Input is badly formatted!\n";exit(-1);}
        } else if (!parsed__CUST_NO__XCOORD__YCOORD__DEMAND__READY_TIME__DUE_DATE__SERVICE_TIME){
            cerr<<"Parsing customer table headings.\n";
            
            parsed__CUST_NO__XCOORD__YCOORD__DEMAND__READY_TIME__DUE_DATE__SERVICE_TIME = true;
            
            istringstream iss(tmp);
            string str;
            iss>>str;
            assert(str == "CUST");
            if(str != "CUST"){cerr<<"Input is badly formatted!\n";exit(-1);}
            iss>>str;
            assert(str == "NO.");
            if(str != "NO."){cerr<<"Input is badly formatted!\n";exit(-1);}
            iss>>str;
            assert(str == "XCOORD.");
            if(str != "XCOORD."){cerr<<"Input is badly formatted!\n";exit(-1);}
            iss>>str;
            assert(str == "YCOORD.");
            if(str != "YCOORD."){cerr<<"Input is badly formatted!\n";exit(-1);}
            iss>>str;
            assert(str == "DEMAND");
            if(str != "DEMAND"){cerr<<"Input is badly formatted!\n";exit(-1);}
            iss>>str;
            assert(str == "READY");
            if(str != "READY"){cerr<<"Input is badly formatted!\n";exit(-1);}
            iss>>str;
            assert(str == "TIME");
            if(str != "TIME"){cerr<<"Input is badly formatted!\n";exit(-1);}
            iss>>str;
            assert(str == "DUE");
            if(str != "DUE"){cerr<<"Input is badly formatted!\n";exit(-1);}
            iss>>str;
            assert(str == "DATE");
            if(str != "DATE"){cerr<<"Input is badly formatted!\n";exit(-1);}
            iss>>str;
            assert(str == "SERVICE");
            if(str != "SERVICE"){cerr<<"Input is badly formatted!\n";exit(-1);}
            iss>>str;
            assert(str == "TIME");
            if(str != "TIME"){cerr<<"Input is badly formatted!\n";exit(-1);}

            
            getline(in, tmp);
            if(tmp.size() != 1){cerr<<"Bad input file : "<<tmp.size()<<endl; exit(-1);}
            assert(tmp.size() == 1);
        } else { // Parsing row of customer table.
            

            istringstream iss(tmp);
            int customerid;
            double x_coordinate;
            double y_coordinate;
            int demand;
            int start__time_window;
            int end__time_window;
            int duration;
            
            iss>>customerid;
            iss>>x_coordinate;
            iss>>y_coordinate;
            iss>>demand;
            iss>>start__time_window;
            iss>>end__time_window;
            iss>>duration;

            x_coordinate *= coordinate_multiplication_factor;
            y_coordinate *= coordinate_multiplication_factor;

            pair<double, double> location(x_coordinate, y_coordinate);
            if(location__to__locationid.find(location) == location__to__locationid.end()){
                location__to__locationid[location] = location_counter++;
                locationid__to__location[location__to__locationid[location]] = location;
            }
            int locationid = location__to__locationid[location];
            
            //cerr<<locationid<<" -to-> "<<location.first<<" x "<<location.second<<endl;

            customer__to__locationid[customerid] = locationid;
            
            customer__to__demand[customerid] = demand;
            customer__to__start_time_window[customerid] = start__time_window;
            customer__to__end_time_window[customerid] = end__time_window;
            customer__to__duration[customerid] = duration;
            customer__to__value[customerid] = global__customer_value;

            /* Setup the route duration constraints based on the depot time windows.*/
            if(customerid == 0){
                for(auto veh_to_cap = vehicleid_to__capacity.begin()
                        ; veh_to_cap != vehicleid_to__capacity.end()
                        ; veh_to_cap++){
                    vehicleid_to__duration[veh_to_cap->first] 
                        = end__time_window - start__time_window; 
                }
            }
        }
    }while(!in.eof());
}

void parse(const string& primaryFileName)
{    
    ifstream file;
    file.open (primaryFileName.c_str());
    parse(file);
    file.close();
}

void write__vrx(){
    cout<<"VRX"<<endl;
    cout<<"NAME "<<problem_name<<endl;
    cout<<"COMMENT Converted from Solomon datafile "<<global__input_filename<<endl;
    cout<<endl;
    
    cout<<"COMMODITIES Load"<<endl;

    cout<<"METRICS"<<endl;
    cout<<"  Dist EUC OBJ MULT 1"<<endl;
    cout<<"  Time EUC MULT 100"<<endl;
    cout<<"*END*"<<endl;
    cout<<"TIME_METRIC Time"<<endl;
    cout<<endl;

    // cout<<"METRICS"<<endl;
    // cout<<"\t Time EUC OBJ MULT 100"<<endl;
    // cout<<"*END*"<<endl;
    // cout<<"TIME_METRIC Time"<<endl;
    // cout<<endl;

    cout<<"LOCATIONS"<<endl;
    for(auto location_info = locationid__to__location.begin()
            ; location_info != locationid__to__location.end()
            ; location_info++){
        cout<<"\t"<<location_info->first
            <<" "<<location_info->second.first
            <<" "<<location_info->second.second
            <<endl;
    }
    cout<<"*END*"<<endl;
    cout<<endl;
    
    cout<<"VEHICLES"<<endl;
    for(auto veh = vehicleid_to__capacity.begin()
            ; veh != vehicleid_to__capacity.end()
            ; veh++){
        cout<<"\t"<<veh->first<<" "<<veh->second<<endl;
    }
    cout<<"*END*"<<endl;
    cout<<endl;
    
    cout<<"VEHICLE_AVAIL"<<endl;
    for(auto veh = vehicleid_to__capacity.begin()
            ; veh != vehicleid_to__capacity.end()
            ; veh++){
        cout<<"\t"<<veh->first<<" "<<0<<" "<<0<<" "<<" "<<0<<" "<<vehicleid_to__duration[veh->first]<<endl;
    }
    cout<<"*END*"<<endl;
    cout<<endl;
    

    cout<<"REQUESTS"<<endl;
    for(auto cust = customer__to__demand.begin()
            ; cust != customer__to__demand.end()
            ; cust++){
        if(cust->first == 0)continue;
        cout<<"\t"<<cust->first
            <<" "<<customer__to__locationid[cust->first]
            <<" "<<customer__to__value[cust->first]
            <<" "<<customer__to__demand[cust->first]
            <<endl;
    }
    cout<<"*END*"<<endl;
    cout<<endl;

    cout<<"REQUEST_TIMES"<<endl;
    for(auto cust = customer__to__demand.begin()
             ; cust != customer__to__demand.end()
             ; cust++){
        if(cust->first == 0)continue;
        cout<<"\t"<<cust->first
            <<" "<<customer__to__start_time_window[cust->first]
            <<" "<<customer__to__end_time_window[cust->first]
            <<" "<<customer__to__duration[cust->first]
            <<endl;
    }
    cout<<"*END*"<<endl;
    cout<<endl;

    
    cout<<"ELISELOLLI 1.0"<<endl;
    cout<<"LOLLIPOP 1.0"<<endl;
    cout<<"MST 1.0"<<endl;
    cout<<"HULLPENALTY 1.0"<<endl;
    cout<<"HULLAREAPENALTY 1.0"<<endl;
    cout<<"ROUTEBENDINGENERGY 1.0"<<endl;
    cout<<endl;
}

int main(int argc, char** argv){

    if(argc == 4 ){
        istringstream iss(argv[3]);
        iss>>coordinate_multiplication_factor;
    }
    
    if( ! ( argc <= 4 && argc >= 3) ) {cerr<<"Bad number of arguments("<<argc<<").\n";exit(-1);}
    assert(argc<=4);
    assert(argc>=3);

    global__customer_value = atoi(argv[2]); 
    global__input_filename = string(argv[1]);
    parse(global__input_filename);
    
    write__vrx();


    return 0;
}
