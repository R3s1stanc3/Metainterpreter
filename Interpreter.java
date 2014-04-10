
/**
 * This is a metemorphic interpreter for my own metalanguage. The metalanguage will be translated in Javacode
 * The interpreter is not finished, so there may be some bugs, but for my purpoeses, it's working
 * 
 * @author R3s1stanc3 [vxnetw0rk] r3s1stanc3@riseup.net
 * @version 0.2
 */

import java . util . Random ;

public class Interpreter
{
    
    public static void main ( String args [ ] )
    {
        
        String code = "var_String,a,\"lololo\"\n" +
            "cond0\n" +
            "if_a<=b\n" +
            "System.out.println(a);\n" +
            "ifend\nelse\n"+
            "System.out.println(b);\nelseend\ncond0end\nSystem.out.println(\"x\");\n" ;
            /*"var_ int, i, 12\n" +
            "var_String,lo,\"aaa\"\n" +
            "System . out . println (i);\n" +
            "var_,i,14\n" +
            "loop0_count,0,<5\n" +
            "loop1_aa,9,<10\n" +
            "System.out.println(count);\n" +
            "loop1end\n" +
            "loop0end\n" +
            "arr_int,troll, ,source.split(\"<<<\")\n" +
            "func0_int,add,int,a,int,b\n" +
            "return a+b;\n" +
            "func0end" ;*/

        for ( int i = 0; i < 10; i++ )
            System . out . println ( interpret( code ) ) ;
          
        
     
            
    }
    
    /**
     * Interprets the metacode. The generated code can directly be compiled
     * @param codeStr The metacode as a string, lines seperated by \n
     * @return One possible representative of the metacode in Java
     */
    public static String interpret ( String codeStr )
    {
        
        String [ ] code = codeStr . split ( "\n" ) ;
        String genCode = "" ;
        for ( int i = 0; i < code . length; i++ )
        {
            // generating variables
            if ( code [ i ] . startsWith ( "var_" ) )
                genCode += declareVar ( code[i] ) + "\n" ;
            
            // generating loops
            else if ( code [ i ] . startsWith ( "loop" ) )
            {
                char loopCount = code [ i ] . charAt ( 4 ) ;
                System . out . println ( "loop" + loopCount + "end" ) ;
                String tmp = "" ;
                while ( !(code[i] . equals ( "loop" + loopCount + "end" )) )
                {
                    tmp += code[i] + "\n";
                    i++ ;
                }
                System . out . println ( tmp ) ;
                genCode += loop ( tmp . split("\n") ) + "\n" ;
            }
            
            // generating functions
            else if ( code [ i ] . startsWith ( "func" ) )
            {
                char funcCount = code [ i ] . charAt ( 4 ) ;
                String tmp = "" ;
                while ( !(code[i] . equals ( "func" + funcCount + "end" )) )
                {
                    tmp += code[i] + "\n" ;
                    i++ ;
                }
                genCode += func ( tmp . split("\n") ) + "\n" ;
            }
            
            else if ( code [ i ] . startsWith ( "cond" ) )
            {
                char condCount = code [ i ] . charAt ( 4 ) ;
                String tmp = "" ;
                i++ ;
                while ( !(code[i] . equals ( "cond" + condCount + "end" )) )
                {
                    tmp += code[i] + "\n" ;
                    i++ ;
                }
                genCode += condition ( tmp . split("\n") ) + "\n" ;
            }
            
            // generating arrays
            else if ( code [ i ] . startsWith ( "arr_" ) )
                genCode += array ( code[i] ) + "\n" ;
            
            // if the instruction is unknown to the interpreter, we'll leave it as it is
            else 
                genCode += code [ i ] + "\n" ;
            
            //genCode += "\n" ;
        }
        
        return genCode ;
        
    }
    
    /**
     * Defining variables with a chance of about 1/4 to add some stupid trash code and a chance of 1/4
     * for strings to be encoded
     * Syntax:
     *  var_type,name,value
     * @param instruction The instruction in our metalanguage
     * @return The generated Java source
     */
    public static String declareVar ( String instruction )
    {
        
        String [ ] split = (instruction . split ( "_" )) ;
        String [ ] params = split[1] . split ( "," ) ;
        params [ 0 ] = params [ 0 ] . replaceAll ( " ", "" ) ;
        params [ 1 ] = params [ 1 ] . replaceAll ( " ", "" ) ;
        String value = interpret ( params[2] ) ;
        
        int rand = new Random() . nextInt(4) ;
        if ( rand == 1 )
            return params[0] + " " + params[1] + "=" + value + ";" ;
        
        else if ( rand == 2 )  
            return params [0] + " " + params [1] + ";\n" + params[1] + "=" + value + ";" ;
        
        // if we got a "pure" string, there is a chance that it will be encoded
        else if ( rand == 3 && params[0] . equals ( "String" ) && value . startsWith ( "\"" ) && value . endsWith ( "\"\n" ) )
            return params [0] + " " + params [1] + "=" + encodeString ( value ) + ";" ;
        
        else
            return varTrash ( params [0], params [1], value ) ;
            
    }
    
    /**
     * Generating loops. Returns a for, while or do-while loop
     * Syntax:
     *  loopX_counter,initial value,condition
     *      instructions to be executed in the loop
     *  loopXend
     * X is the number of the current loop. Used to generate a loop in a loop
     * @param instr The loop block from the metacode as a string array
     * @return A representative of the loop in Java code
     */
    public static String loop ( String [ ] instr )
    {
        
        int length = instr . length ;
        // params: 0 - counter, 1 - start, 2 - condition
        String [ ] params = (instr[0].split("_"))[1] . split ( "," ) ;
        params [ 0 ] = params [ 0 ] . replaceAll ( " ", "" ) ;
        params [ 1 ] = params [ 1 ] . replaceAll ( " ", "" ) ;
        params [ 2 ] = params [ 2 ] . replaceAll ( " ", "" ) ;
        
        // metacode that will be executed in the loop. will be interpreted later ;)
        String metacode = "" ;
        for ( int i = 1; i < instr . length; i++ )
            metacode += instr[i] + "\n" ;
        
        int rand = new Random() . nextInt ( 3 ) ;
        if ( rand == 1 )
            return "int " + params[0] + "=" + params[1] + ";\nwhile (" + params[0] + params[2] + "){\n" + interpret(metacode) + params[0] + "++;\n}" ;
        else if ( rand == 2 )
            return "int " + params[0] + "=" + params[1] + ";\ndo{\n" + interpret(metacode) + params[0] + "++;\n}\nwhile(" + params[0] + params[2] + ");" ;
        else
            return "for(int " + params[0] + "=" + params[1] + ";" + params[0] + params[2] + ";" + params[0] + "++){\n" + interpret(metacode) + "}" ;
        
    }
    
    /**
     * Generating an array
     * Syntax:
     *  arr_type,name,length,value
     * @param instr The definition of the array in metacode
     * @return The definition of the array in Java code
     */
    public static String array ( String instr )
    {
        
        // params: 0 - type; 1 - name; 2 - length; 3 - value
        String [ ] params = (instr.split("_"))[1] . split ( "," ) ;
        
        return params[0] + " " + params[1] + "[" + params[2] + "]=" + interpret(params[3]) + ";" ;
        
    }
    
    /**
     * Generates a function from the metacode
     * Syntax:
     *  funcX_type,name,param1-type,param1,param2-type,param2,...
     *      instructions to be executed in the function
     *  funcXend
     * e.g. func0_String,someFunc,String,a,int,b
     * @param instr The function block from the metacode as a string array
     * @return The function as Java code
     */
    public static String func ( String [ ] instr )
    {
        
        //int length = instr . length ;
        // params: 0 - type, 1 - name, 2*X - paramXtype, 2*X +1 - paramXname
        String [ ] params = (instr[0].split("_"))[1] . split ( "," ) ;
        
        // metacode to be executed in the funktion. will be interpreted later ;)
        String metacode = "" ;
        for ( int i = 1; i < instr . length; i++ )
            metacode += instr[i] + "\n" ;
        
        String funcBody = "public static " + params[0] + " " + params[1] + "(" ;
        for (int i = 2; i < params . length-3; i=i+2 )
        {
            funcBody += params[i] + " " + params[i+1] + "," ;
        }
        funcBody += params[params.length-2] + " " + params[params.length-1] + "){\n" + interpret(metacode) + "\n}" ;
        
        return funcBody ;
        
    }
    
    /**
     * Generates a different representation for a plain text string
     * @param value String to encode
     * @return String in an encoded form
     */
    public static String encodeString ( String value )
    {
        
        String encoded = "" ;
        // first and last char are " so we skip them
        for ( int i = 1; i < value . length()-3; i++ )
        {
            encoded += "String.valueOf((char)" + (int)value.charAt(i) + ")+" ;
        }
        return encoded + "String.valueOf((char)" + (int)value.charAt(value.length()-3) + ")" ;
        
    }
    
    /**
     * Syntax:
     *  condX
     *   if_condition
     *      instructions to be executed in the if block
     *   ifend
     *   else
     *      instructions to be executed in the else block
     *   elseend
     *  condXend
     *  
     *  Don't use more than one condition ( "a<b && c==d" won't work )
     */
    public static String condition ( String [ ] instr )
    {
        
        String cond = instr [ 0 ] . split ( "_" ) [ 1 ] ;
        String metacode_if = "", metacode_else = "", ret = "" ;
        int rand = new Random() . nextInt ( 2 ) ;
        int i ;
        
        for ( i = 1; instr[i].indexOf("ifend") == -1; i++ )
            metacode_if += instr [ i ] + "\n" ;
            
        i += 2 ;
        for ( i = i; instr[i].indexOf("elseend") == -1; i++ )
            metacode_else += instr [ i ] + "\n" ;
            
        if ( cond . indexOf ( "<=" ) != -1 )
        {
            String parts [ ] = cond . split ( "<=" ) ;
            if ( rand == 1 ) cond = parts[1] + ">=" + parts[0] ;
        }
        else if ( cond . indexOf ( ">=" ) != -1 )
        {
            String parts [ ] = cond . split ( ">=" ) ;
            if ( rand == 1 ) cond = parts[1] + "<=" + parts[0] ;
        }
        else if ( cond . indexOf ( "<" ) != -1 )
        {
            String parts [ ] = cond . split ( "<" ) ;
            if ( rand == 1 ) cond = parts[1] + ">" + parts[0] ;
        }
        else if ( cond . indexOf ( ">" ) != -1 )
        {
            String parts [ ] = cond . split ( ">" ) ;
            if ( rand == 1 ) cond = parts[1] + "<" + parts[0] ;
        }
        
        // 50% chance to swap the if and else block
        if ( new Random().nextInt(2) == 1 )
        {
            cond = "!(" + cond + ")" ;
            ret += "if (" + cond + "){\n" + interpret ( metacode_else ) + "\n}\n" + "else {\n" + interpret ( metacode_if ) + "\n}\n" ;
        }
        else
        {
            ret += "if (" + cond + "){\n" + interpret ( metacode_if ) + "\n}\n" + "else {\n" + interpret ( metacode_else ) + "\n}\n"  ;
        }
        
        return ret ;
        
    }
    
    /**
     * Adds some trashcode to the variable declaration by defining it with a random value, before defining it with the original value
     * @param type Type of the variable
     * @param name Name of the variable
     * @param value Value of the variable
     * @return var declaration with some senseless trash
     */
    public static String varTrash ( String type, String name, String value )
    {
        
        if ( type . equals ( "String" ) )
        {
            if ( new Random() . nextInt(2) == 1 )
                return type + " " + name + ";\n" + name + "=\"" + randStr(10) + "\";\n" + name + "=" + value + ";" ; 
            else return "" ;
        }
        
        else if ( type . equals ( "int" ) ) // int
            return type + " " + name + ";\n" + name + "=" + (new Random() . nextInt(999)) + ";\n" + name + "=" + value + ";" ;
            
        else
            return type + " " + name + ";\n" + name + "=" + value + ";" ;
            
    }
    
    /**
     * Returns a random string
     * @param l Length of the generated string
     * @return A random String of length l
     */
    public static String randStr ( int l )
    {
        
        Random rand = new Random() ;
        String list = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789" ;
        String ret = "" ;
        ret += list . charAt ( rand.nextInt(52) ) ;
        
        for ( int i = 0; i < l - 1; i++ )
        {
            ret += list . charAt ( rand.nextInt(62) ) ;
        }
        
        return ret ;
        
    }
    
}
