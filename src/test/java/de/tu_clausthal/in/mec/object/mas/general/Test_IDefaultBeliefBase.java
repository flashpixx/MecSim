package de.tu_clausthal.in.mec.object.mas.general;

import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.jason.general.CLiteral;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * test class for generic belief base
 */
public class Test_IDefaultBeliefBase
{
    /**
     * initializes a predefined beliefbase
     *
     * @return predefined beliefbase
     */
    private IBeliefBase<Literal> generateTestset()
    {
        final IBeliefBase<Literal> l_beliefbase = new IDefaultBeliefBase<Literal>(  ){};

        // add some inherited beliefbases
        l_beliefbase.add( new CPath( "aa1" ), new IDefaultBeliefBase<Literal>(  ){} );
        l_beliefbase.add( new CPath( "aa2/bb1/cc1/dd1" ), new IDefaultBeliefBase<Literal>(  ){} );

        return l_beliefbase;
    }

    @Test
    public void testGetBeliefbase()
    {
        final IBeliefBase<Literal> l_beliefbase= this.generateTestset();

        // create new beliefbase to add
        final IBeliefBase<Literal> l_testBeliefbase = new IDefaultBeliefBase<Literal>(){};
        l_testBeliefbase.add( CPath.EMPTY, new CLiteral( ASSyntax.createLiteral( "test" ) ) );

        // add to some existing paths
        l_beliefbase.add( new CPath( "aa3" ), l_testBeliefbase );
        l_beliefbase.add( new CPath( "aa1/bb1" ), l_testBeliefbase );
        l_beliefbase.add( new CPath( "aa1/bb1/cc1/dd1" ), l_testBeliefbase );

        // add to some non-existing paths
        l_beliefbase.add( new CPath( "aa4/bb2/cc3/dd4" ), l_testBeliefbase );

        // check correct addition
        assertEquals( l_beliefbase.get( "aa3" ), l_testBeliefbase );
        assertEquals( l_beliefbase.get( "aa1/bb1" ), l_testBeliefbase );
        assertEquals( l_beliefbase.get( "aa1/bb1/cc1/dd1" ), l_testBeliefbase );
        assertEquals( l_beliefbase.get( "aa4/bb2/cc3/dd4" ), l_testBeliefbase );
    }

    @Test
    public void testAddEmptyPath()
    {
        final IBeliefBase<Literal> l_beliefbase = new IDefaultBeliefBase<Literal>(  ){};

        try
        {
            l_beliefbase.add( CPath.EMPTY, new IDefaultBeliefBase<Literal>(  ){} );
            fail("expected empty path exception");
        }
        catch(IllegalArgumentException l_exception)
        {
            // test passed
        }
    }

    @Test
    public void testAddLiteral( )
    {
        // generate testset and literal
        final IBeliefBase<Literal> l_beliefbase= this.generateTestset();
        final CLiteral l_testLiteral = new CLiteral( ASSyntax.createLiteral( "test" ) );

        // push literal into top-level set
        l_beliefbase.add( CPath.EMPTY, l_testLiteral );

        // push literal into some existing inherited beliefbase
        l_beliefbase.add( "aa1", l_testLiteral );
        l_beliefbase.add( "aa2/bb1", l_testLiteral );

        // push literal into some non-existing inherited beliefbase
        l_beliefbase.add( "aa3", l_testLiteral );
        l_beliefbase.add( "aa4/bb1/cc1", l_testLiteral );

        assertTrue( l_beliefbase.getTopLiterals().contains( l_testLiteral ) );
        assertTrue( l_beliefbase.getTopLiterals( new CPath( "aa1" ) ).contains( l_testLiteral ) );
        assertTrue( l_beliefbase.getTopLiterals( new CPath( "aa2/bb1" ) ).contains( l_testLiteral ) );
        assertTrue( l_beliefbase.getTopLiterals( new CPath( "aa3" ) ).contains( l_testLiteral ) );
        assertTrue( l_beliefbase.getTopLiterals( new CPath( "aa4/bb1/cc1" ) ).contains( l_testLiteral ) );
    }
}
