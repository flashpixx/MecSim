// scrambling value in [0,1] (0 low scrambling, 1 high scrambling)
ag_scramble(0.4).

// initial goal
!drive.

// acceleration (based on Nagel-Schreckenberg model)
+!accelerate
   :  m_speed(Speed) &
      m_acceleration(Accelerate) &
      m_maxspeed(MaxSpeed)
   <- .min([MaxSpeed, Speed+Accelerate], NewSpeed);
      .println("Accelerate -> set actual speed ", Speed, " to new speed ", NewSpeed); 
      set(self, m_speed, NewSpeed);
      !drive.

// deceleration (based on Nagel-Schreckenberg model)
+!decelerate
   :  m_speed(Speed) &
      m_deceleration(Decelerate) &
      Decelerate > 0
   <- .max([5, Speed-Decelerate], NewSpeed);
      .println("Decelerate -> set actual speed ", Speed, " to new speed ", NewSpeed); 
      set(self, m_speed, NewSpeed);
      !drive.

// if there is a predecessor, check braking distance
// braking distance will be calculated using gaussian sum
// @see https://de.wikipedia.org/wiki/Gau%C3%9Fsche_Summenformel
+!drive
    :    m_speed(Speed) &
         m_deceleration(Deceleration) &
    	 ag_predecessor([Predecessor]) &
	     not (.empty([Predecessor]))

    <-   // this beliefs will be updated
	     .abolish(speed_range(_));
         .abolish(ag_distance(_)); 	 
         .abolish(ag_predecessor(_));
      
	     // get distance to predecessing car
         Predecessor =.. [X|_];
         mecsim.literal2number(X,Distance);
         
	     // add the speed range
         for ( .range(I, 1, math.ceil( Speed / 10 ) ) )
         {
             +speed_range(I * 10);
         }

	     // calculate braking distance with gaussian sum
         UpperSumIndex = math.floor( Speed / Deceleration );
         BrakingDistance = UpperSumIndex * ( Speed - 0.5 * Deceleration * ( UpperSumIndex + 1 ) );

	 	// check if predecessing car is too close
	 	if ( BrakingDistance > Scramble*Distance )
    	 {		  	     		
		!decelerate;
    	 }
    	 else
    	 {
		!accelerate;		
	 }.

// default behaviour - accelerate
+!drive
   :  true
   <- !accelerate.
