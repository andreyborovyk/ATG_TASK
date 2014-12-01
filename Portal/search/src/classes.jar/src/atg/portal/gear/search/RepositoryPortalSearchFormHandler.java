/*<ATGCOPYRIGHT>
 * Copyright (C) 2002-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.portal.gear.search;

import atg.servlet.*;
import atg.repository.*;
import atg.repository.servlet.*;
import atg.droplet.GenericFormHandler;
import atg.portal.framework.search.*;
import atg.portal.framework.search.repository.*;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.service.perfmonitor.PerfStackMismatchException;

import java.util.*;
import java.io.IOException;
import javax.servlet.*;

/**
 *  Description of the Class
 *
 * @author    sdrye
 * @version $Id: //app/portal/version/10.0.3/search/classes.jar/src/atg/portal/gear/search/RepositoryPortalSearchFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class RepositoryPortalSearchFormHandler extends GenericFormHandler
{

  //-------------------------------------
  /**  Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/search/classes.jar/src/atg/portal/gear/search/RepositoryPortalSearchFormHandler.java#2 $$Change: 651448 $";

  private final static String sSearchHandlerPath = "/atg/portal/framework/search/repository/RepositoryPortalSearchHandler";

  private int mStartIndex = 0;
  private int mEndIndex = 0;
  private int mMaxResults = -1;
  private int mMaxResultsPerRepository = -1;
  private int mMaxGearsPerRepository = -1;

  private String mSuccessURL;
  private String mErrorURL;
  private String mKeywords;
  private Collection mAllSearchResults;
  private int mResultsPerPage;
  private int mCurrentResultPageNum;
  private int mTotalResultsNum;
  private int mTotalResultsPageNum;
  private String mCommunityId;
  private String mGearId;
  private String mPageId;
  private String mSearchScope;
  private ArrayList mAllResultsList;

  /**
   *  Sets property SuccessURL. If search is successful, hander redirects client
   *  to this URL.
   *
   * @param  pURL  a String property that is the URL to redirect to.
   * @beaninfo     description: a String property that is the URL to redirect
   *      to.
   */
  public void setSuccessURL(String pURL)
  {
    mSuccessURL = pURL;
  }

  /**
   *  Sets property ErrorURL. If search fails, hander redirects client to this
   *  URL. For example, RepositoryException thrown
   *
   * @param  pURL  a String property that is the URL to redirect to.
   * @beaninfo     description: a String property that is the URL to redirect
   *      to.
   */
  public void setErrorURL(String pURL)
  {
    mErrorURL = pURL;
  }

  /**
   *  Sets the keywords attribute of the RepositoryPortalSearchFormHandler
   *  object
   *
   * @param  pKeywords  The new keywords value
   */
  public void setKeywords(String pKeywords)
  {
    mKeywords = pKeywords;
  }

  /**
   *  Sets the startIndex attribute of the RepositoryPortalSearchFormHandler
   *  object
   *
   * @param  pStartIndex  The new startIndex value
   */
  public void setStartIndex(int pStartIndex)
  {
    mStartIndex = pStartIndex;
  }

  /**
   *  Sets the endIndex attribute of the RepositoryPortalSearchFormHandler
   *  object
   *
   * @param  pEndIndex  The new endIndex value
   */
  public void setEndIndex(int pEndIndex)
  {
    mEndIndex = pEndIndex;
  }

  /**
   *  Sets the resultsPerPage attribute of the RepositoryPortalSearchFormHandler
   *  object
   *
   * @param  pResultsPerPage  The new resultsPerPage value
   */
  public void setResultsPerPage(int pResultsPerPage)
  {
    mResultsPerPage = pResultsPerPage;
  }

  /**
   *  Sets the currentResultPageNum attribute of the
   *  RepositoryPortalSearchFormHandler object
   *
   * @param  pCurrentResultPageNum  The new currentResultPageNum value
   */
  public void setCurrentResultPageNum(int pCurrentResultPageNum)
  {
    mCurrentResultPageNum = pCurrentResultPageNum;

    mStartIndex = (mCurrentResultPageNum - 1) * mResultsPerPage;
    mEndIndex = mStartIndex + mResultsPerPage;

    if (mEndIndex > mTotalResultsNum)
    {
      mEndIndex = mTotalResultsNum;
    }
  }

  /**
   *  Sets the totalResultsNum attribute of the
   *  RepositoryPortalSearchFormHandler object
   *
   * @param  pTotalResultsNum  The new totalResultsNum value
   */
  public void setTotalResultsNum(int pTotalResultsNum)
  {
    mTotalResultsNum = pTotalResultsNum;
  }

  /**
   *  Sets the totalResultsPageNum attribute of the
   *  RepositoryPortalSearchFormHandler object
   *
   * @param  pTotalResultsPageNum  The new totalResultsPageNum value
   */
  public void setTotalResultsPageNum(int pTotalResultsPageNum)
  {
    mTotalResultsPageNum = pTotalResultsPageNum;
  }

  /**
   *  Sets the communityId attribute of the RepositoryPortalSearchFormHandler
   *  object
   *
   * @param  pCommunityId  The new communityId value
   */
  public void setCommunityId(String pCommunityId)
  {
    mCommunityId = pCommunityId;
  }

  /**
   *  Sets the gearId attribute of the RepositoryPortalSearchFormHandler object
   *
   * @param  pGearId  The new gearId value
   */
  public void setGearId(String pGearId)
  {
    mGearId = pGearId;
  }

  /**
   *  Sets the pageId attribute of the RepositoryPortalSearchFormHandler object
   *
   * @param  pPageId  The new pageId value
   */
  public void setPageId(String pPageId)
  {
    mPageId = pPageId;
  }

  /**
   *  Sets the searchScope attribute of the RepositoryPortalSearchFormHandler
   *  object
   *
   * @param  pSearchScope  The new searchScope value
   */
  public void setSearchScope(String pSearchScope)
  {
    mSearchScope = pSearchScope;
  }

  /**
   *  Set the maximum number of results to return. Set to -1 for no limit.
   *  Default is -1. This does not restrict the results on basis of relevance.
   *  It simply checks for this limit after searching each repository so a low
   *  value of this limit with a very high results count for a single repository
   *  can potentially block the remaining repositories from being searched.
   *
   * @param  pMaxResults  The new maxResults value
   */
  public void setMaxResults(int pMaxResults)
  {
    mMaxResults = pMaxResults;
  }

  /**
   *  Set the maximum number of results to return per repository. Set to -1 for
   *  no limit. Default is -1.
   *
   * @param  pMaxResultsPerRepository  The new maxResultsPerRepository value
   */
  public void setMaxResultsPerRepository(int pMaxResultsPerRepository)
  {
    mMaxResultsPerRepository = pMaxResultsPerRepository;
  }

  /**
   *  Set the maximum number of results to return per repository. Set to -1 for
   *  no limit. Default is -1.
   *
   * @param  pMaxGearsPerRepository  The new maxGearsPerRepository value
   */
  public void setMaxGearsPerRepository(int pMaxGearsPerRepository)
  {
    mMaxGearsPerRepository = pMaxGearsPerRepository;
  }

  /**
   *  Returns property SuccessURL
   *
   * @return    The value of the property SuccessURL.
   */
  public String getSuccessURL()
  {
    return mSuccessURL;
  }

  /**
   *  Returns property ErrorURL
   *
   * @return    The value of the property ErrorURL.
   */
  public String getErrorURL()
  {
    return mErrorURL;
  }

  /**
   *  Gets the keywords attribute of the RepositoryPortalSearchFormHandler
   *  object
   *
   * @return    The keywords value
   */
  public String getKeywords()
  {
    return mKeywords;
  }

  /**
   *  Gets the allSearchResults attribute of the
   *  RepositoryPortalSearchFormHandler object
   *
   * @return    The allSearchResults value
   */
  public Collection getAllSearchResults()
  {
    return mAllSearchResults;
  }

  /**
   *  Gets the startIndex attribute of the RepositoryPortalSearchFormHandler
   *  object
   *
   * @return    The startIndex value
   */
  public int getStartIndex()
  {
    return mStartIndex;
  }

  /**
   *  Gets the startCount attribute of the RepositoryPortalSearchFormHandler
   *  object
   *
   * @return    The startCount value
   */
  public int getStartCount()
  {
    return mStartIndex + 1;
  }

  /**
   *  Gets the endIndex attribute of the RepositoryPortalSearchFormHandler
   *  object
   *
   * @return    The endIndex value
   */
  public int getEndIndex()
  {
    return mEndIndex;
  }

  /**
   *  Gets the endCount attribute of the RepositoryPortalSearchFormHandler
   *  object
   *
   * @return    The endCount value
   */
  public int getEndCount()
  {
    return mEndIndex;
  }

  /**
   *  Gets the searchResults attribute of the RepositoryPortalSearchFormHandler
   *  object
   *
   * @return    The searchResults value
   */
  public Collection getSearchResults()
  {
    if (mStartIndex >= 0 && mEndIndex <= mTotalResultsNum)
    {
      return mAllResultsList.subList(mStartIndex, mEndIndex);
    }
    else
    {
      return null;
    }
  }

  /**
   *  Gets the resultsPerPage attribute of the RepositoryPortalSearchFormHandler
   *  object
   *
   * @return    The resultsPerPage value
   */
  public int getResultsPerPage()
  {
    return mResultsPerPage;
  }

  /**
   *  Gets the currentResultPageNum attribute of the
   *  RepositoryPortalSearchFormHandler object
   *
   * @return    The currentResultPageNum value
   */
  public int getCurrentResultPageNum()
  {
    return mCurrentResultPageNum;
  }

  /**
   *  Gets the totalResultsNum attribute of the
   *  RepositoryPortalSearchFormHandler object
   *
   * @return    The totalResultsNum value
   */
  public int getTotalResultsNum()
  {
    return mTotalResultsNum;
  }

  /**
   *  Gets the totalResultsPageNum attribute of the
   *  RepositoryPortalSearchFormHandler object
   *
   * @return    The totalResultsPageNum value
   */
  public int getTotalResultsPageNum()
  {
    return mTotalResultsPageNum;
  }

  /**
   *  Gets the communityId attribute of the RepositoryPortalSearchFormHandler
   *  object
   *
   * @return    The communityId value
   */
  public String getCommunityId()
  {
    return mCommunityId;
  }

  /**
   *  Gets the gearId attribute of the RepositoryPortalSearchFormHandler object
   *
   * @return    The gearId value
   */
  public String getGearId()
  {
    return mGearId;
  }

  /**
   *  Gets the pageId attribute of the RepositoryPortalSearchFormHandler object
   *
   * @return    The pageId value
   */
  public String getPageId()
  {
    return mPageId;
  }

  /**
   *  Gets the searchScope attribute of the RepositoryPortalSearchFormHandler
   *  object
   *
   * @return    The searchScope value
   */
  public String getSearchScope()
  {
    return mSearchScope;
  }

  /**
   *  Get the maximum numbers of results to return
   *
   * @return    The maxResults value
   */
  public int getMaxResults()
  {
    return mMaxResults;
  }

  /**
   *  Get the maximum numbers of results to return per repository
   *
   * @return    The maxResultsPerRepository value
   */
  public int getMaxResultsPerRepository()
  {
    return mMaxResultsPerRepository;
  }

  /**
   *  Get the maximum numbers of results to return per repository
   *
   * @return    The maxGearsPerRepository value
   */
  public int getMaxGearsPerRepository()
  {
    return mMaxGearsPerRepository;
  }

  /**
   *  Description of the Method
   *
   * @param  pRequest              Description of Parameter
   * @param  pResponse             Description of Parameter
   * @return                       Description of the Returned Value
   * @exception  ServletException  Description of Exception
   * @exception  IOException       Description of Exception
   */
  public boolean handleSearch(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse)
     throws ServletException, IOException
  {
    RepositoryPortalSearchHandler reph = (RepositoryPortalSearchHandler)pRequest.resolveName(sSearchHandlerPath, true);

    // reph shouldn't be null but just in case
    if (reph != null)
    {
      if (PerformanceMonitor.isEnabled())
      {
        PerformanceMonitor.startOperation("Repository Search Gear form handler", "handleSearch");
      }

      reph.setDynRequest(pRequest);
      reph.setDynResponse(pResponse);

      reph.setKeywords(getKeywords());
      reph.setMaxResults(getMaxResults());
      reph.setMaxResultsPerRepository(getMaxResultsPerRepository());
      reph.setMaxGearsPerRepository(getMaxGearsPerRepository());

      try
      {

        if ("portal".equals(getSearchScope()))
        {
          mAllSearchResults = reph.searchPortal();
        }
        else if ("community".equals(getSearchScope()))
        {
          mAllSearchResults = reph.searchCommunity(getCommunityId());
        }
        else if ("gear".equals(getSearchScope()))
        {
          mAllSearchResults = reph.searchGear(getCommunityId(), getGearId());
        }
        else if ("page".equals(getSearchScope()))
        {
          mAllSearchResults = reph.searchPage(getPageId());
        }
      }

      catch (PortalSearchException e)
      {
        if (isLoggingError())
        {
          logError(e);
        }
      }

      initResultsPageProps();

    }

    if (PerformanceMonitor.isEnabled())
    {
      try
      {
        PerformanceMonitor.endOperation("Repository Search Gear form handler", "handleSearch");
      }
      catch (PerfStackMismatchException e)
      {
      }

    }

    return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
  }

  /**  Description of the Method */
  private void initResultsPageProps()
  {
    setTotalResultsNum(0);
    setTotalResultsPageNum(0);
    setStartIndex(0);
    setEndIndex(0);

    if (mAllSearchResults != null)
    {

      setTotalResultsNum(mAllSearchResults.size());
      setCurrentResultPageNum(1);
      mAllResultsList = new ArrayList();
      mAllResultsList.addAll(mAllSearchResults);

      int totalPageNum = getTotalResultsNum() / getResultsPerPage();
      if ((getTotalResultsNum() % getResultsPerPage()) > 0)
      {
        totalPageNum++;
      }
      setTotalResultsPageNum(totalPageNum);

    }
  }
}

